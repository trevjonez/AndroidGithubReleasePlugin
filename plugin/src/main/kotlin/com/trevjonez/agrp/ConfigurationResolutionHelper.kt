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

import com.trevjonez.github.defaultRetrofit
import com.trevjonez.github.releases.Release
import org.gradle.api.GradleException
import org.gradle.api.Project

class ConfigurationResolutionHelper(
    val project: Project,
    val configs: Set<AgrpConfigExtension>
) {
  val createRequest: Release.Request by lazy {
    Release.Request(
        tagName,
        targetCommitish,
        releaseName,
        releasebody,
        draft,
        preRelease)
  }

  val releaseApi: Release.Api
    get() {
      project.extensions.findByType(Release.Api::class.java)?.let { return it }

      val url = apiUrl
      val retrofit = if (url.isNullOrBlank()) {
        defaultRetrofit()
      } else {
        defaultRetrofit()
            .newBuilder()
            .baseUrl(url)
            .build()
      }

      return retrofit.create(Release.Api::class.java).also {
        project.extensions.add("githubReleaseService", it)
      }
    }

  val apiUrl: String? by lazy {
    cascadeOptionalLookup({ it.apiUrl }, "apiUrl")
  }

  val owner: String by lazy {
    cascadeLookup({ it.owner }, "owner", CharSequence::isNotBlank)
  }

  val repo: String by lazy {
    cascadeLookup({ it.repo }, "repo", CharSequence::isNotBlank)
  }

  val accessToken: String by lazy {
    cascadeLookup({ it.accessToken }, "accessToken", CharSequence::isNotBlank)
  }

  val tagName: String by lazy {
    applyModifiers(
        cascadeLookup(
            { it.tagName },
            "tagName",
            CharSequence::isNotBlank
        )
    )
  }

  val targetCommitish: String? by lazy {
    cascadeOptionalLookup({ it.targetCommitish }, "targetCommitish")
  }

  val releaseName: String? by lazy {
    cascadeOptionalLookup({ it.releaseName }, "releaseName")
  }

  val releasebody: String? by lazy {
    cascadeOptionalLookup({ it.releasebody }, "releasebody")
  }

  val draft: Boolean? by lazy {
    cascadeOptionalLookup({ it.draft }, "draft")
  }

  val preRelease: Boolean? by lazy {
    cascadeOptionalLookup({ it.preRelease }, "preRelease")
  }

  val overwrite: Boolean by lazy {
    cascadeOptionalLookup({ it.overwrite }, "overwrite") ?: false
  }

  private fun <T : Any> cascadeLookup(
      fieldLookup: (AgrpConfigExtension) -> T?,
      fieldName: String,
      isValid: (T) -> Boolean = { true }
  ): T {
    val result = configs.mapNotNull { fieldLookup.invoke(it) }.firstOrNull()

    if (result == null || !isValid.invoke(result))
      throw GradleException("Invalid `$fieldName` config value: $result")

    return result
  }

  private fun <T : Any> cascadeOptionalLookup(
      fieldLookup: (AgrpConfigExtension) -> T?,
      fieldName: String,
      isValid: (T) -> Boolean = { true }
  ): T? {
    val result = configs.mapNotNull { fieldLookup.invoke(it) }.firstOrNull()

    if (result != null && !isValid.invoke(result))
      throw GradleException("Invalid `$fieldName` config value: $result")

    return result
  }

  private fun applyModifiers(tagName: CharSequence): String {
    var result = tagName
    configs.forEach {
      if (it.tagModifier != null)
        result = it.tagModifier!!.transform(result.toString())
    }
    return result.toString()
  }
}