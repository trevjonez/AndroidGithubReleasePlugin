/*
 * Copyright (c) 2019. Trevor Jones
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

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.plugins.ExtensionAware

abstract class AbsAgrpPlugin : Plugin<Project> {

  protected lateinit var project: Project
  private lateinit var baseExtension: AgrpBaseExtension

  abstract fun registerTasksForVariants()

  private fun createExtension() {
    baseExtension = project.extensions.create("AndroidGithubRelease", AgrpBaseExtension::class.java, project)
  }

  override fun apply(target: Project) {
    project = target
    createExtension()
//    project.afterEvaluate {
    registerTasksForVariants()
//    }
  }

  protected fun registerTasksForVariant(variant: BaseVariant) {
    val configHelper = ConfigurationResolutionHelper(
      project,
      gatherConfigExtensions(project, variant)
    )

    val createTask = project.tasks.register(
      "create${variant.name.capitalize()}GithubRelease",
      CreateReleaseTask::class.java
    ) {
      it.group = AGRP_GROUP
      it.description = "Create a release/tag on github for the \"${variant.name}\" build variant"
      it.config = configHelper
    }

    val variantAssetTasks = configHelper.configs
      .flatMap { it.assets }
      .mapIndexed { index, asset ->
        project.tasks.register(
          "upload${variant.name.capitalize()}Asset$index",
          UploadReleaseAssetTask::class.java
        ) {
          it.group = AGRP_GROUP
          it.description = "Upload the asset \"$it\" to a release on github for the \"${variant.name}\" build variant"
          it.createTask = createTask
          it.assetFile.set(project.file(asset))
          it.config = configHelper
        }.dependsOn(createTask)
      }

    project.tasks.register("upload${variant.name.capitalize()}Assets", DefaultTask::class.java) {
      it.group = AGRP_GROUP
      it.description = "Upload all assets to a release on github for the \"${variant.name}\" build variant"
    }.dependsOn(variantAssetTasks)
  }

  private fun gatherConfigExtensions(project: Project, variant: BaseVariant): Set<AgrpConfigExtension> =
      LinkedHashSet<AgrpConfigExtension>().apply {
        add((baseExtension as ExtensionAware).extensions.getByName("defaultConfig") as AgrpConfigExtension)

        //Flavors in order
        variant.productFlavors.forEach {
          addOrLog({ baseExtension.androidConfigs.getByName(it.name) },
            "No AndroidGithubReleasePlugin config with name \"${it.name}\"",
            project)
        }

        //Debug / Release
        addOrLog({ baseExtension.androidConfigs.getByName(variant.buildType.name) },
          "No AndroidGithubReleasePlugin config with name \"${variant.buildType.name}\"",
          project)

        //Full variant name
        addOrLog({ baseExtension.androidConfigs.getByName(variant.name) },
          "No AndroidGithubReleasePlugin config with name \"${variant.name}\"",
          project)
      }

  private fun <T> MutableSet<T>.addOrLog(action: () -> T, message: String, project: Project) {
    try {
      this.add(action.invoke())
    } catch (e: UnknownDomainObjectException) {
      project.logger.info(message)
    }
  }

  companion object {
    const val AGRP_GROUP = "Android Github Release Plugin"
  }
}