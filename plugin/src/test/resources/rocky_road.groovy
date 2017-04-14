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

buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath group: 'com.android.tools.build', name: 'gradle', version: '2.3.1'
  }
}

plugins {
  id 'com.trevjonez.AndroidGithubReleasePlugin'
}

apply plugin: 'com.android.application'

android {
  buildToolsVersion '25.0.2'
  compileSdkVersion 25

  defaultConfig {
    applicationId 'com.test.script'
    minSdkVersion 16
    targetSdkVersion 25
    versionCode 1
    versionName '1.0.0'
  }

  flavorDimensions 'ice_cream', 'nuts'

  productFlavors {
    chocolate {
      dimension 'ice_cream'
    }
    fudge {
      dimension 'ice_cream'
    }

    peanut {
      dimension 'nuts'
    }
    pecan {
      dimension 'nuts'
    }
  }

  variantFilter {
    if (it.getFlavors().get(0).name == "fudge" && it.getFlavors().get(1).name == "peanut")
      it.setIgnore(true)
  }
}

repositories {
  jcenter()
}

Properties properties = new Properties()
properties.load(project.file('local.properties').newDataInputStream())

AndroidGithubRelease {

  defaultConfig {
    tagName "0.3.0"
    owner "trevjonez"
    repo "PluginTesting"
    accessToken properties.getProperty("GITHUB_API_TOKEN")
    overwrite true
    assets 'build.gradle'
  }

  androidConfigs {
    debug {
      preRelease true
      tagModifier { it + "D" }
    }
    release {
      tagModifier { it + "R" }
    }

    chocolate {
      tagModifier { it + "C" }
    }
    fudge {
      tagModifier { it + "F" }
    }

    peanut {
      tagModifier { it + "p" }
    }
    pecan {
      tagModifier { it + "P" }
    }
  }
}
