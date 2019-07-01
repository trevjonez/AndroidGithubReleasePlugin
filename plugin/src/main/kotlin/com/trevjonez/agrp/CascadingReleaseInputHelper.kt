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

import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class CascadingReleaseInputHelper(
  private val project: Project,
  val configs: Set<AgrpConfigExtension>
) : ReleaseConfig {

  @get:Input
  override val targetCommitish by lazy { cascadeConventions { targetCommitish } }
  @get:[Input Optional]
  override val releaseName by lazy { cascadeConventions { releaseName } }
  @get:[Input Optional]
  override val releaseBody by lazy { cascadeConventions { releaseBody } }
  @get:Input
  override val draft by lazy { cascadeConventions { draft } }
  @get:Input
  override val preRelease by lazy { cascadeConventions { preRelease } }
  @get:Input
  override val overwrite by lazy { cascadeConventions { overwrite } }
  @get:Input
  override val tagName by lazy { cascadeConventions { tagName } }

  override val tagModifier: Property<Transformer<String, String>> by lazy {
    (project.objects.property(Transformer::class.java)
        as Property<Transformer<String, String>>).also { tagModProp ->
      tagModProp.set(Transformer { rawTag ->
        modifiers.mapNotNull { it.orNull }.fold(rawTag) { tag, next -> next.transform(tag) }
      })
    }
  }

  @get:[Input Optional]
  val modifiers: List<Property<Transformer<String, String>>> by lazy {
    configs.map { it.tagModifier }
  }

  val modifiedTagName: String by lazy {
    tagModifier.get().transform(tagName.get())
  }

  inline fun <T : Any> cascadeConventions(crossinline fieldLookup: AgrpConfigExtension.() -> Property<T>) = configs
    .map { it.fieldLookup() }
    .windowed(2) { (lower, higher) -> higher.convention(lower) }
    .last()
}