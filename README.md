AndroidGithubReleasePlugin
=====
The android github release plugin is a gradle plugin that works along side with the android gradle plugin to create github releases, and upload build outputs to the new release.

The DSL is designed to mimic the android build plugin DSL for type and flavor configuration. 
   
Installation & Usage
--------------------
In the root `build.gradle` file add the appropriate jitpack repository and classpath dependency. 
```groovy
buildscript {
    repositories {
        maven { url "https://jitpack.io" }
    }
    dependencies {
        classpath 'com.github.trevjonez:AndroidGithubReleasePlugin:1.1.0'
    }
}
```

In the app or library module apply and configure the plugin as desired
```groovy
apply plugin: 'com.trevjonez.AndroidGithubReleasePlugin'

AndroidGithubRelease {
  defaultConfig {
    tagName "1.0.0"
    owner "RepoOwner"
    repo "RepoName"
    accessToken GITHUB_API_TOKEN
    assets 'build/outputs'
  }

  androidConfigs {
    debug {
      preRelease true
      tagModifier { it + "-DEBUG" } // "1.0.0-DEBUG"
    }
    release {
      draft true
      releaseName "Release Name Here"
    }
  }
}
```

The `defaultConfig` block as well as the named blocks have the following api options

- apiUrl(String) default: `https://api.github.com`, base url for github api
- owner(String) **required**, username or organization name
- repo(String) **required**, name of the repository
- accessToken(String) **required**, key requires `repo` permission
- tagName(String) **required**, name of new or existing tag
- targetCommitish(String) default: `master`, see [github api docs](https://developer.github.com/v3/repos/releases/#create-a-release) for details
- releaseName(String) _optional_, release title
- releasebody(String) _optional_, body content of release
- draft(boolean) default: `false`, flags the github release as a draft
- preRelease(boolean) default: `false`, flags the github release as a pre-release
- overwrite(boolean) default: `false`, allows release meta data to be updated, existing binaries must be manually deleted prior to re-upload
- tagModifier(Transformer<String, String>) _optional_, transformer for dynamic tag naming based on combined configuration
- assets(String vararg) _optional_, file or directory names relative to module directory, Any `asset` that is a directory will be zipped before being uploaded

### Advanced Config
For builds with flavors the named blocks are applied in a cascading override manner:
```groovy
AndroidGithubRelease {
  defaultConfig {
    tagName "A"
  }

  androidConfigs {
    debug {
      tagName "B"
    }
    release {
      ...
    }
    
    red {
      tagName "C"
    }
    blue {
      ...
    }
    
    redDebug {
      tagName "D"
    }
  }
}
```

The cascade order is `flavor(s)`->`type`->`fullVariantName`.
Following this ordering the above configs associated `createRedDebugGithubRelease` task would create a tag named "D", `createRedReleaseGithubRelease` would be "C", `createBlueDebugGithubRelease` would be "B", `createBlueReleaseGithubRelease` would be "A"

In a multi dimensional build the flavors are cascaded in the same order as listed in the variant name.

There are two exceptions to the cascading override:
 1. `tagModifier` string transformers which are applied in the cascade order
 2. `assets` do not override but are combined into a set

Tasks
-----
Each build variant will create a `createFlavorTypeGithubRelease` task which creates/updates the github release

Each asset to upload will create a task named `uploadFlavorTypeAsset0`...`uploadFlavorTypeAssetN`
All numbered upload asset tasks depend on the create release task,

Each build variant also creates a `uploadFlavorTypeAssets` task which functionally does nothing however the task automatically depends on all upload asset tasks. This serves to provide a clean hook point for making other tasks depend on the success of the github release tasks

License
-------
    Copyright 2016 Trevor Jones

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

Running project tests
-------
In order to run tests you must first create a repository to test against on github and update the three test cases configs to point at that test repo.
From that point you create a `local.properties` file in `src/test/resources` and add the `GITHUB_API_TOKEN` as well as the normal `sdk.dir` property. 