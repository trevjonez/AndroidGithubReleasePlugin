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

import com.trevjonez.github.gradle.GithubApiTask
import com.trevjonez.github.releases.Release
import org.gradle.api.tasks.Nested

abstract class AgrpTask : GithubApiTask() {
  @get:Nested
  lateinit var config: CascadingReleaseInputHelper

  val releaseApi by githubApi<Release.Api>()
}