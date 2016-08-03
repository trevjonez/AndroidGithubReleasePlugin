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

package com.trevjonez.agrp.github

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @author TrevJonez
 */
interface ReleaseService {
  @GET("/repos/{owner}/{repo}/releases/tags/{tag}")
  fun getRelease(@Path("owner") owner: String,
                 @Path("repo") repo: String,
                 @Path("tag") tag: String,
                 @Header("Authorization") oAuth2: String): Call<ReleaseResponse>

  @Headers("Content-Type: application/json; charset=utf-8")
  @POST("/repos/{owner}/{repo}/releases")
  fun postRelease(@Path("owner") owner: String,
                  @Path("repo") repo: String,
                  @Body pendingRelease: PendingRelease,
                  @Header("Authorization") oAuth2: String): Call<ReleaseResponse>

  @Headers("Content-Type: application/json; charset=utf-8")
  @PATCH("/repos/{owner}/{repo}/releases/{id}")
  fun patchRelease(@Path("owner") owner: String,
                   @Path("repo") repo: String,
                   @Path("id") id: String,
                   @Body pendingRelease: PendingRelease,
                   @Header("Authorization") oAuth2: String): Call<ReleaseResponse>

  @Multipart
  @POST("//{upload_url}/repos/{owner}/{repo}/releases/{id}/assets")
  fun uploadReleaseAsset(@Path("upload_url") uploadUrl: String,
                         @Path("owner") owner: String,
                         @Path("repo") repo: String,
                         @Path("id") id: String,
                         @Query("name") fileName: String,
                         @Part file: MultipartBody.Part,
                         @Header("Content-Type") contentType: String,
                         @Header("Authorization") oAuth2: String): Call<Asset>
}