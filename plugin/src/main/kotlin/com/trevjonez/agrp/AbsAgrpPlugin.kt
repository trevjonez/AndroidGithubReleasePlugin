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
import com.trevjonez.github.gradle.GithubApiPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskProvider

abstract class AbsAgrpPlugin : GithubApiPlugin() {

  private lateinit var agrpExtension: AgrpExtension

  override fun createConfigExt() {
    agrpExtension = target.extensions.create("AndroidGithubRelease", AgrpExtension::class.java, target)
  }

  protected fun registerTasksForVariant(variant: BaseVariant) {
    val configHelper = CascadingReleaseInputHelper(target, gatherConfigExtensions(variant))

    val createTask = target.tasks.register(
      "create${variant.name.capitalize()}GithubRelease",
      CreateReleaseTask::class.java
    ) {
      it.group = AGRP_GROUP
      it.description = "Create a release/tag on github for the \"${variant.name}\" build variant"
      it.config = configHelper
      it.setApiConfig(configExtension)
    }

    val variantAssetTasks: List<TaskProvider<UploadReleaseAssetTask>> =
        configHelper.configs
          .flatMap { it.assets }
          .mapIndexed { index, asset ->
            target.tasks.register("upload${variant.name.capitalize()}Asset$index", UploadReleaseAssetTask::class.java) {
              it.group = AGRP_GROUP
              it.description =
                  "Upload the asset \"$it\" to a release on github for the \"${variant.name}\" build variant"
              it.createTask = createTask
              it.assetFile.set(asset)
              it.config = configHelper
              it.setApiConfig(configExtension)
              it.dependsOn(createTask)
            }
          }

    target.tasks.register("upload${variant.name.capitalize()}Assets", DefaultTask::class.java) {
      it.group = AGRP_GROUP
      it.description = "Upload all assets to a release on github for the \"${variant.name}\" build variant"
      it.dependsOn(variantAssetTasks)
    }
  }

  private fun gatherConfigExtensions(variant: BaseVariant): Set<AgrpConfigExtension> {
    val defaultConfig = agrpExtension.extensions.getByName("defaultConfig") as AgrpConfigExtension
    val candidates = variant.productFlavors.map { it.name } + variant.buildType.name + variant.name
    return (listOf(defaultConfig) + candidates.mapNotNull { agrpExtension.androidConfigs.findByName(it) }).toSet()
  }

  companion object {
    const val AGRP_GROUP = "Android Github Release Plugin"
  }
}