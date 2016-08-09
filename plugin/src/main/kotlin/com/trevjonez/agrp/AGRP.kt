/*
 * Copyright (c) 2016. Trevor Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trevjonez.agrp

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.io.File
import java.util.*

/**
 * @author TrevJonez
 */
class AGRP : Plugin<Project> {
  lateinit var baseExtension: AgrpBaseExtension

  companion object {
    val AGRP_GROUP = "Android Github Release Plugin"
  }

  override fun apply(project: Project) {
    baseExtension = project.extensions.create("AndroidGithubRelease", AgrpBaseExtension::class.java, project)

    project.afterEvaluate(validateAndAddTasks())
  }

  private fun validateAndAddTasks(): Action<Project> {
    return Action { project ->

      val androidExtension = project.extensions.getByName("android")
              ?: throw NullPointerException("Android configuration not found on the current project. \n" +
              "AndroidGithubRelease is only applicable to android builds")

      when (androidExtension) {
        is AppExtension -> androidExtension.applicationVariants.all(createTasks(project))
        is LibraryExtension -> androidExtension.libraryVariants.all(createTasks(project))
        is TestExtension -> androidExtension.applicationVariants.all(createTasks(project))
      }
    }
  }

  private fun createTasks(project: Project): Action<BaseVariant> {
    return Action { variant ->
      project.logger.info("Creating tasks for variant: \"${variant.name}\"")

      val configs = gatherConfigExtensions(project, variant)

      val createTaskOptions = LinkedHashMap<String, Any>()
      createTaskOptions.put("name", "create${variant.name.capitalize()}GithubRelease")
      createTaskOptions.put("type", CreateReleaseTask::class.java)
      createTaskOptions.put("group", AGRP_GROUP)
      createTaskOptions.put("description", "Create a release/tag on github for the \"${variant.name}\" build variant")

      //The system creates the instance here so we have to do the DI immediately following instantiation
      val createReleaseTask = project.tasks.create(createTaskOptions) as CreateReleaseTask
      createReleaseTask.configs = configs

      val assetUploadTasks = LinkedList<UploadReleaseAssetTask>()
      val assets = createReleaseTask.assets()
      assets.forEach {
        val asset = File(project.projectDir.path + File.separatorChar + it)
        project.logger.info("Asset path: ${asset.absolutePath}")

        val uploadAssetOptions = LinkedHashMap<String, Any>()
        uploadAssetOptions.put("name", "upload${variant.name.capitalize()}Asset${assets.indexOf(it)}")
        uploadAssetOptions.put("type", UploadReleaseAssetTask::class.java)
        uploadAssetOptions.put("group", AGRP_GROUP)
        uploadAssetOptions.put("description", "Upload the asset \"${asset.name}\" to a release on github for the \"${variant.name}\" build variant")
        uploadAssetOptions.put("dependsOn", listOfNotNull(createReleaseTask.name))

        val uploadAssetTask = project.tasks.create(uploadAssetOptions) as UploadReleaseAssetTask
        uploadAssetTask.createTask = createReleaseTask

        assetUploadTasks.add(uploadAssetTask)
      }

      val uploadAssetsOptions = LinkedHashMap<String, Any>()
      uploadAssetsOptions.put("name", "upload${variant.name.capitalize()}Assets")
      uploadAssetsOptions.put("type", DefaultTask::class.java)
      uploadAssetsOptions.put("group", AGRP_GROUP)
      uploadAssetsOptions.put("description", "Upload all assets to a release on github for the \"${variant.name}\" build variant")
      uploadAssetsOptions.put("dependsOn", assetUploadTasks)

      project.tasks.create(uploadAssetsOptions)
    }
  }

  private fun gatherConfigExtensions(project: Project, variant: BaseVariant): Set<AgrpConfigExtension> {
    val configs = LinkedHashSet<AgrpConfigExtension>()

    configs.add((baseExtension as ExtensionAware).extensions.getByName("defaultConfig") as AgrpConfigExtension)

    //Debug / Release
    configs.addOrLog({ baseExtension.androidConfigs.getByName(variant.buildType.name) }, "No AGRP config with name \"${variant.buildType.name}\"", project)

    //Flavors in order
    variant.productFlavors.forEach {
      configs.addOrLog({ baseExtension.androidConfigs.getByName(it.name) }, "No AGRP config with name \"${it.name}\"", project)
    }

    //Full variant name
    configs.addOrLog({ baseExtension.androidConfigs.getByName(variant.name) }, "No AGRP config with name \"${variant.name}\"", project)

    //Keep track of config usage, we will throw warnings later
    configs.forEach { it.consumed = true }

    return configs
  }
}