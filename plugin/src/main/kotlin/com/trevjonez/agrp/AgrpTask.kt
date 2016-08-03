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

import com.trevjonez.agrp.github.ReleaseService
import com.trevjonez.agrp.okhttp.HeaderInterceptor
import okhttp3.OkHttpClient
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * @author TrevJonez
 */
abstract class AgrpTask : DefaultTask() {

  lateinit var configs: Set<AgrpConfigExtension>

  protected fun releaseService(): ReleaseService {
    val okhttp3 = OkHttpClient.Builder().addInterceptor(HeaderInterceptor("Accept", "application/vnd.github.v3+json")).build()
    val retrofit = Retrofit.Builder()
            .client(okhttp3)
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(baseUrl())
            .build()

    return retrofit.create(ReleaseService::class.java)
  }

  private fun baseUrl(): String {
    var result: String? = null
    configs.forEach { if (it.apiUrl != null) result = it.apiUrl }

    if (result == null || result!!.trim().length == 0) throw GradleException("No valid api url found")

    return result!!
  }
}