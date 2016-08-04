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
class Asset {
  var url: String? = null
  var browser_download_url: String? = null
  var id: Int? = null
  var name: String? = null
  var label: String? = null
  var state: String? = null
  var content_type: String? = null
  var size: Int? = null
  var download_count: Int? = null
  var created_at: String? = null
  var updated_at: String? = null
  var uploader: User? = null

  override fun toString(): String {
    return "Asset(url=$url, browser_download_url=$browser_download_url, id=$id, name=$name, label=$label, state=$state, content_type=$content_type, size=$size, download_count=$download_count, created_at=$created_at, updated_at=$updated_at, uploader=$uploader)"
  }
}