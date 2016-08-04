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

import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

/**
 * @author TrevJonez
 */
open class AgrpBaseExtension(project: Project) {
  val androidConfigs: NamedDomainObjectContainer<AgrpConfigExtension>

  init {
    androidConfigs = project.container(AgrpConfigExtension::class.java)
    val defaultConfig = (this as ExtensionAware).extensions.create("defaultConfig", AgrpConfigExtension::class.java, "defaultConfig")
    defaultConfig.apiUrl = "https://api.github.com"
  }

  fun androidConfigs(closure: Closure<Any>) {
    androidConfigs.configure(closure)
  }
}