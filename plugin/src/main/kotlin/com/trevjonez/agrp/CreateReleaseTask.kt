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

import org.gradle.api.tasks.TaskAction

/**
 * @author TrevJonez
 */
open class CreateReleaseTask : AgrpTask() {

  @TaskAction
  fun createRelease() {
    //getReleaseByTagName
    //if the release exists check if the config allows overwriting, else fail build
    //if the release doesn't exist or we are ok to overwrite, post/patch the info up, be sure to hold the ID and asset upload url
  }
}