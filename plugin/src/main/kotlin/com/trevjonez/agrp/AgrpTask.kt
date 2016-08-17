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

import com.trevjonez.agrp.github.PendingRelease
import com.trevjonez.agrp.github.ReleaseService
import com.trevjonez.agrp.okhttp.HeaderInterceptor
import okhttp3.OkHttpClient
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

/**
 * @author TrevJonez
 */
abstract class AgrpTask : DefaultTask() {

  lateinit var configs: Set<AgrpConfigExtension>

  var releaseService: ReleaseService? = null

  private var pendingRelease: PendingRelease? = null

  fun releaseService(): ReleaseService {
    //Cache the releaseService so multiple calls don't re allocate and any upload tasks can use the same instance

    if (releaseService == null) {

//      val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
//              .addInterceptor(loggingInterceptor)

      val okhttp3 = OkHttpClient.Builder()
              .addInterceptor(HeaderInterceptor("Accept", "application/vnd.github.v3+json"))
              .build()

      val retrofit = Retrofit.Builder()
              .client(okhttp3)
              .addConverterFactory(MoshiConverterFactory.create())
              .baseUrl(apiUrl())
              .build()

      releaseService = retrofit.create(ReleaseService::class.java)

    }

    return releaseService!!
  }

  private fun apiUrl(): String {
    return cascadeLookup({ it.apiUrl }, "apiUrl", validString())!!
  }

  fun owner(): String {
    return cascadeLookup({ it.owner }, "owner", validString())!!
  }

  fun repo(): String {
    return cascadeLookup({ it.repo }, "repo", validString())!!
  }

  fun accessToken(): String {
    return cascadeLookup({ it.accessToken }, "accessToken", validString())!!
  }

  fun pendingRelease(): PendingRelease {
    if (pendingRelease == null) {
      pendingRelease = PendingRelease()
      pendingRelease?.tag_name = tagName()
      pendingRelease?.target_commitish = targetCommitish()
      pendingRelease?.name = releaseName()
      pendingRelease?.body = releasebody()
      pendingRelease?.draft = draft()
      pendingRelease?.prerelease = preRelease()
    }

    return pendingRelease!!
  }

  private fun tagName(): String {
    return applyModifiers(cascadeLookup({ it.tagName }, "tagName", validString())!!)
  }

  private fun targetCommitish(): String? {
    return cascadeLookup({ it.targetCommitish }, optional = true)
  }

  private fun releaseName(): String? {
    return cascadeLookup({ it.releaseName }, optional = true)
  }

  private fun releasebody(): String? {
    return cascadeLookup({ it.releasebody }, optional = true)
  }

  private fun draft(): Boolean? {
    return cascadeLookup({ it.draft }, optional = true)
  }

  private fun preRelease(): Boolean? {
    return cascadeLookup({ it.preRelease }, optional = true)
  }

  fun overwrite(): Boolean {
    return cascadeLookup({ it.overwrite }, optional = true) ?: false
  }

  private fun validString(): (String) -> Boolean = { it.trim().length > 0 }

  private fun <T> cascadeLookup(fieldLookup: (AgrpConfigExtension) -> T?,
                                fieldName: String = "",
                                isValid: (T) -> Boolean = { true },
                                optional: Boolean = false): T? {
    var result: T? = null
    configs.forEach {
      val lookup = fieldLookup.invoke(it)
      if (lookup != null)
        result = lookup
    }

    if (!optional && (result == null || !isValid.invoke(result!!)))
      throw GradleException("Invalid `$fieldName` config value: $result")

    return result
  }

  fun assets(): Set<String> {
    val result: MutableSet<String> = LinkedHashSet()
    configs.forEach { result.addAll(it.assets) }
    return result
  }

  private fun applyModifiers(tagName: String): String {
    var result = tagName
    configs.forEach { result = it.tagModifier?.transform(result) ?: result }
    return result
  }
}