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

plugins {
  id 'com.trevjonez.AndroidGithubReleasePlugin'
  id 'com.android.application'
}

android {
  compileSdkVersion 28

  defaultConfig {
    applicationId 'com.test.script'
    minSdkVersion 23
    targetSdkVersion 28
    versionCode 1
    versionName '1.0.0'
  }

  flavorDimensions "taste"
  productFlavors {
    vanilla {
      dimension "taste"
    }
    chocolate {
      dimension "taste"
    }
    strawberry {
      dimension "taste"
    }
  }
}

repositories {
  jcenter()
  google()
}

GithubApi {
  owner "trevjonez"
  repo "PluginTesting"
  authToken pluginTestingToken
}

AndroidGithubRelease {

  defaultConfig {
    tagName "0.4.0"
    overwrite true
  }

  androidConfigs {
    debug {
      preRelease true
      assets 'build.gradle'
      tagModifier { it + "D" }
    }
    release {
      tagModifier { it + "R" }
    }

    vanilla {
      assets 'local.properties'
      tagModifier { it + "V" }
    }
    chocolate {
      tagModifier { it + "C" }
    }
    strawberry {
      tagModifier { it + "S" }
    }
  }
}
