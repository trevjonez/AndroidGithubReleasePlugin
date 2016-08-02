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
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.util.*

/**
 * @author TrevJonez
 */
class AGRP : Plugin<Project> {
  override fun apply(project: Project) {
    project.extensions.create("AndroidGithubRelease", AgrpConfigBaseExtension::class.java)
    val baseExtension = project.extensions.findByName("AndroidGithubRelease") as ExtensionAware
    val flypeContainer = project.container(AgrpConfigExtension::class.java)
    baseExtension.extensions.add("androidConfigs", flypeContainer)

    project.afterEvaluate(validateAndAddTasks())
  }

  private fun validateAndAddTasks(): Action<Project> {
    return Action { project ->

      val androidExtension = project.extensions.getByName("android")
              ?: throw NullPointerException("Android configuration not found on the current project. \n" +
              "AndroidGithubRelease is only applicable to android builds")

      when (androidExtension) {
        is AppExtension -> androidExtension.applicationVariants.all(createAndUpdateTasks(project))
        is LibraryExtension -> androidExtension.libraryVariants.all(createAndUpdateTasks(project))
        is TestExtension -> androidExtension.applicationVariants.all(createAndUpdateTasks(project))
      }
    }
  }

  private fun createAndUpdateTasks(project: Project): Action<BaseVariant> {
    return Action { variant ->
      project.logger.info("Creating tasks for variant: \"${variant.name}\"")
      val configs = LinkedList<AgrpConfigExtension>()

      @Suppress("UNCHECKED_CAST")
      val configContainer = project.extensionByPath(NamedDomainObjectContainer::class.java, "AndroidGithubRelease", "androidConfigs") as NamedDomainObjectContainer<AgrpConfigExtension>
      configs.addOrLog({ configContainer.getByName(variant.buildType.name) }, "No AGRP config with name \"${variant.buildType.name}\"", project)
      variant.productFlavors.forEach {
        configs.addOrLog({ configContainer.getByName(it.name) }, "No AGRP config with name \"${it.name}\"", project)
      }
    }
  }
}