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

/**
 * @author TrevJonez
 */
class ReleaseResponse {
  var url: String? = null
  var html_url: String? = null
  var assets_url: String? = null
  var upload_url: String? = null
  var tarball_url: String? = null
  var zipball_url: String? = null
  var id: Int? = null
  var tag_name: String? = null
  var target_commitish: String? = null
  var name: String? = null
  var body: String? = null
  var draft: Boolean? = null
  var prerelease: Boolean? = null
  var created_at: String? = null
  var published_at: String? = null
  var author: User? = null
  var assets: MutableList<Asset?>? = null

  override fun toString(): String {
    return "ReleaseResponse(url=$url, html_url=$html_url, assets_url=$assets_url, upload_url=$upload_url, tarball_url=$tarball_url, zipball_url=$zipball_url, id=$id, tag_name=$tag_name, target_commitish=$target_commitish, name=$name, body=$body, draft=$draft, prerelease=$prerelease, created_at=$created_at, published_at=$published_at, author=$author, assets=$assets)"
  }
}