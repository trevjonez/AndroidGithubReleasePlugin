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

/**
 * @author TrevJonez
 */
open class AgrpConfigExtension(val name: String) {
  var apiUrl: String? = null
  var owner: String? = null
  var repo: String? = null
  var accessToken: String? = null
  var tagName: String? = null
  var targetCommitish: String? = null
  var releaseName: String? = null
  var releasebody: String? = null
  var draft = false
  var preRelease = false
}