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
class User {
  var login: String? = null
  var id: Int? = null
  var avatar_url: String? = null
  var gravatar_id: String? = null
  var url: String? = null
  var html_url: String? = null
  var followers_url: String? = null
  var following_url: String? = null
  var gists_url: String? = null
  var starred_url: String? = null
  var subscriptions_url: String? = null
  var organizations_url: String? = null
  var repos_url: String? = null
  var events_url: String? = null
  var received_events_url: String? = null
  var type: String? = null
  var site_admin: Boolean? = null

  override fun toString(): String {
    return "User(login=$login, id=$id, avatar_url=$avatar_url, gravatar_id=$gravatar_id, url=$url, html_url=$html_url, followers_url=$followers_url, following_url=$following_url, gists_url=$gists_url, starred_url=$starred_url, subscriptions_url=$subscriptions_url, organizations_url=$organizations_url, repos_url=$repos_url, events_url=$events_url, received_events_url=$received_events_url, type=$type, site_admin=$site_admin)"
  }
}