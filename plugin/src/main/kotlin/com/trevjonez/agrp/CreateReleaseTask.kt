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

import com.trevjonez.agrp.github.ReleaseResponse
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import retrofit2.Call

/**
 * @author TrevJonez
 */
open class CreateReleaseTask : AgrpTask() {

  lateinit var response: ReleaseResponse

  @TaskAction
  fun createRelease() {

    val releaseLookupResponse = releaseService().getRelease(owner(), repo(), pendingRelease().tag_name!!, "token ${accessToken()}").execute()

    if (releaseLookupResponse.isSuccessful && !overwrite()) {
      throw GradleException("A release with the specified tag name already exists.\n" +
              "You can configure this task to overwrite the release @ that tag name with `overwrite = true`\n" +
              releaseLookupResponse.body().toString())
    }

    val postPatchCall: Call<ReleaseResponse>?
    if (releaseLookupResponse.isSuccessful) {
      postPatchCall = releaseService().patchRelease(owner(), repo(), releaseLookupResponse.body().id!!, pendingRelease(), "token ${accessToken()}")
    } else {
      postPatchCall = releaseService().postRelease(owner(), repo(), pendingRelease(), "token ${accessToken()}")
    }

    val postPatchResponse = postPatchCall.execute()

    if (!postPatchResponse.isSuccessful) {
      throw GradleException("The ${postPatchCall.request().method()} github api call failed:\n${postPatchResponse.errorBody().string()}\n")
    }

    response = postPatchResponse.body()

    println("Github release created: ${response.html_url}")
  }
}