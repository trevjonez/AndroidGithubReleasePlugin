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

buildscript {
  repositories {
    jcenter()
    google()
  }
  dependencies {
    classpath group: 'com.android.tools.build', name: 'gradle', version: '3.4.0'
  }
}

plugins {
  id 'com.trevjonez.AndroidGithubReleasePlugin'
}

apply plugin: 'com.android.application'

android {
  compileSdkVersion 28

  defaultConfig {
    applicationId 'com.test.script'
    minSdkVersion 23
    targetSdkVersion 28
    versionCode 1
    versionName '1.0.0'
  }
}

repositories {
  jcenter()
  google()
}

Properties properties = new Properties()
properties.load(project.file('local.properties').newDataInputStream())

AndroidGithubRelease {

  defaultConfig {
    tagName "0.3.0"
    owner "trevjonez"
    repo "PluginTesting"
    accessToken properties.getProperty("GITHUB_API_TOKEN")
    assets 'build.gradle'
    overwrite true
  }

  androidConfigs {
    debug {
      preRelease true
      assets 'testDirectory'
    }
    release {
      assets 'local.properties'
    }
  }
}
