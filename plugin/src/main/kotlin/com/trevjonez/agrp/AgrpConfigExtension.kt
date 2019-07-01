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

import com.trevjonez.github.gradle.dslFun
import org.gradle.api.Transformer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

interface ReleaseConfig {
  val tagName: Property<String>
  val targetCommitish: Property<String>
  val releaseName: Property<String>
  val releaseBody: Property<String>
  val draft: Property<Boolean>
  val preRelease: Property<Boolean>
  val overwrite: Property<Boolean>
  val tagModifier: Property<Transformer<String, String>>
}

interface AgrpConfig : ReleaseConfig {
  val assets: ConfigurableFileCollection
}

abstract class AgrpConfigExtension(val name: String, objects: ObjectFactory) : AgrpConfig, ExtensionAware {

  override val tagModifier: Property<Transformer<String, String>> =
      objects.property(Transformer::class.java) as Property<Transformer<String, String>>

  init {
    targetCommitish.convention("master")
    draft.convention(false)
    preRelease.convention(false)
    overwrite.convention(false)
  }

  fun tagName(value: Any) = tagName.dslFun(value)
  fun targetCommitish(value: Any) = targetCommitish.dslFun(value)
  fun releaseName(value: Any) = releaseName.dslFun(value)
  fun releaseBody(value: Any) = releaseBody.dslFun(value)
  fun draft(value: Any) = draft.dslFun(value)
  fun preRelease(value: Any) = preRelease.dslFun(value)
  fun overwrite(value: Any) = overwrite.dslFun(value)
  fun tagModifier(value: Transformer<String, String>) = tagModifier.dslFun(value)
  fun tagModifier(value: Provider<Transformer<String, String>>) = tagModifier.dslFun(value)
  fun assets(vararg values: Any) {
    this.assets.from(values)
  }
}