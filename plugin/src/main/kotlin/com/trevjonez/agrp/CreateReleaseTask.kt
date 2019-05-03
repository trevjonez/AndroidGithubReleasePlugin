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

import com.trevjonez.github.releases.Release
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

open class CreateReleaseTask : AgrpTask() {

  lateinit var response: Release

  @TaskAction
  fun createRelease() {

    val releaseLookupResponse = config.releaseApi.byTag(
      config.owner,
      config.repo,
      config.createRequest.tag_name,
      "token ${config.accessToken}"
    ).execute()

    if (releaseLookupResponse.isSuccessful && !config.overwrite) {
      throw GradleException("A release with the specified tag name already exists.\n" +
          "You can configure this task to overwrite the release @ that tag name with `overwrite = true`\n" +
          releaseLookupResponse.body().toString())
    }
    val existingRelease = releaseLookupResponse.body()
    val postPatchCall = if (releaseLookupResponse.isSuccessful) {
      config.releaseApi.edit(
        config.owner,
        config.repo,
        existingRelease!!.id,
        "token ${config.accessToken}",
        config.createRequest)
    } else {
      config.releaseApi.create(
        config.owner,
        config.repo,
        "token ${config.accessToken}",
        config.createRequest
      )
    }

    val postPatchResponse = postPatchCall.execute()

    if (!postPatchResponse.isSuccessful) {

      val method = postPatchCall.request().method()
      project.logger.lifecycle("$method github release api call failed with code: ${postPatchResponse.code()}")
      throw GradleException("$method github release api call failed:\n${postPatchResponse.errorBody()!!.string()}\n")
    }

    response = postPatchResponse.body()!!

    project.logger.lifecycle("Github release created: ${response.html_url}")
  }
}