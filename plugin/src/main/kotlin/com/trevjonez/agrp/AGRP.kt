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
import org.gradle.api.*
import org.gradle.api.plugins.ExtensionAware
import java.io.File
import java.util.*

/**
 * @author TrevJonez
 */
@Suppress("unused")
class AGRP : Plugin<Project> {
  lateinit var baseExtension: AgrpBaseExtension

  companion object {
    const val AGRP_GROUP = "Android Github Release Plugin"
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
      val variantTasks = LinkedList<Task>()

      //The system creates the instance so we have to do the DI immediately following instantiation in the apply block
      project.createTask(
              type = CreateReleaseTask::class.java,
              name = "create${variant.name.capitalize()}GithubRelease",
              description = "Create a release/tag on github for the \"${variant.name}\" build variant").apply createTask@ {
        this.configs = configs
        variantTasks.add(this)
        for (it in assets) {
          project.createTask(
                  type = UploadReleaseAssetTask::class.java,
                  name = "upload${variant.name.capitalize()}Asset${assets.indexOf(it)}",
                  description = "Upload the asset \"$it\" to a release on github for the \"${variant.name}\" build variant",
                  dependsOn = listOfNotNull(this)).apply {
            createTask = this@createTask
            assetFile = File(project.projectDir.path + File.separatorChar + it)
            project.logger.info("Asset path: ${assetFile.absolutePath}")
            variantTasks.add(this)
          }
        }
      }

      project.createTask(
              type = DefaultTask::class.java,
              name = "upload${variant.name.capitalize()}Assets",
              description = "Upload all assets to a release on github for the \"${variant.name}\" build variant",
              dependsOn = variantTasks)
    }
  }

  private fun <T : DefaultTask> Project.createTask(type: Class<T>, name: String, group: String = AGRP_GROUP, description: String? = null, dependsOn: List<Task>? = null) =
          type.cast(project.tasks.create(LinkedHashMap<String, Any>().apply {
            put("name", name)
            put("type", type)
            put("group", group)
            description?.let { put("description", it) }
            dependsOn?.let { put("dependsOn", it) }
          }))

  private fun gatherConfigExtensions(project: Project, variant: BaseVariant): Set<AgrpConfigExtension> = LinkedHashSet<AgrpConfigExtension>().apply {
    add((baseExtension as ExtensionAware).extensions.getByName("defaultConfig") as AgrpConfigExtension)

    //Flavors in order
    variant.productFlavors.forEach {
      addOrLog({ baseExtension.androidConfigs.getByName(it.name) }, "No AGRP config with name \"${it.name}\"", project)
    }

    //Debug / Release
    addOrLog({ baseExtension.androidConfigs.getByName(variant.buildType.name) }, "No AGRP config with name \"${variant.buildType.name}\"", project)

    //Full variant name
    addOrLog({ baseExtension.androidConfigs.getByName(variant.name) }, "No AGRP config with name \"${variant.name}\"", project)
  }
}