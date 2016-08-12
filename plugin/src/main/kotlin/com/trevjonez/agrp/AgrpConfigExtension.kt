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

import org.gradle.api.Transformer
import java.util.*

/**
 * @author TrevJonez
 */
@Suppress("unused")
open class AgrpConfigExtension(val name: String) {

  var apiUrl: String? = null
  var owner: String? = null
  var repo: String? = null
  var accessToken: String? = null
  var tagName: String? = null

  var targetCommitish: String? = null
  var releaseName: String? = null
  var releasebody: String? = null
  var draft: Boolean? = null
  var preRelease: Boolean? = null

  var overwrite: Boolean? = null
  var tagModifier: Transformer<String, String>? = null

  internal val assets = LinkedHashSet<String>()

  fun assets(vararg assetPaths: String) {
    this.assets.addAll(assetPaths)
  }

  fun apiUrl(apiUrl: String) {
    this.apiUrl = apiUrl
  }

  fun owner(owner: String) {
    this.owner = owner
  }

  fun repo(repo: String) {
    this.repo = repo
  }

  fun accessToken(accessToken: String) {
    this.accessToken = accessToken
  }

  fun tagName(tagName: String) {
    this.tagName = tagName
  }

  fun targetCommitish(targetCommitish: String) {
    this.targetCommitish = targetCommitish
  }

  fun releaseName(releaseName: String) {
    this.releaseName = releaseName
  }

  fun releasebody(releasebody: String) {
    this.releasebody = releasebody
  }

  fun draft(draft: Boolean) {
    this.draft = draft
  }

  fun preRelease(preRelease: Boolean) {
    this.preRelease = preRelease
  }

  fun overwrite(overwrite: Boolean) {
    this.overwrite = overwrite
  }

  fun tagModifier(tagModifier: Transformer<String, String>) {
    this.tagModifier = tagModifier
  }
}