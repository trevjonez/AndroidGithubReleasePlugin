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

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume.assumeNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class AGRPTest {
  @get:Rule
  var testProjectDir = TemporaryFolder()

  lateinit var buildFile: File
  lateinit var settingsFile: File
  lateinit var localDotPropertiesFile: File
  lateinit var gradleDotPropertiesFile: File

  val agpVersion by systemProperty()
  val pluginTestingToken by systemProperty()

  @Before
  fun setUp() {
    buildFile = testProjectDir.newFile("build.gradle")

    gradleDotPropertiesFile = testProjectDir.newFile("gradle.properties")
    gradleDotPropertiesFile.writeText("agpVersion=$agpVersion\n")
    gradleDotPropertiesFile.appendText("pluginTestingToken=$pluginTestingToken\n")

    val settings = File(javaClass.classLoader.getResource("settings.groovy").path)
    settingsFile = testProjectDir.newFile("settings.gradle")
    settingsFile.writeBytes(settings.readBytes())

    val localProp = File(javaClass.classLoader.getResource("local.properties").path)
    localDotPropertiesFile = testProjectDir.newFile("local.properties")
    localDotPropertiesFile.writeBytes(localProp.readBytes())
  }

  @Test
  fun vanilla() {
    val testDir = testProjectDir.newFolder("testDirectory")
    val innerDir = File(testDir, "innerDir")
    innerDir.mkdir()
    val emptyFile = File(testDir, "empty.txt")
    emptyFile.createNewFile()

    val buildScript = File(javaClass.classLoader.getResource("vanilla.groovy").path)
    buildFile.writeBytes(buildScript.readBytes())

    val buildResult = GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
      .withArguments("tasks", "uploadDebugAssets", "--stacktrace")
      .forwardOutput()
      .build()

    assertThat(buildResult.output)
      .contains(
        "Android Github Release Plugin tasks",

        "createDebugGithubRelease - Create a release/tag on github",
        "uploadDebugAsset0 - Upload the asset", "build.gradle\" to github",
        "uploadDebugAsset1 - Upload the asset", "testDirectory\" to github",
        "uploadDebugAssets - Upload all assets to github (lifecycle task)",

        "createReleaseGithubRelease - Create a release/tag on github",
        "uploadReleaseAsset0 - Upload the asset", "build.gradle\" to github",
        "uploadReleaseAsset1 - Upload the asset", "local.properties\" to github",
        "uploadReleaseAssets - Upload all assets to github (lifecycle task)",

        "> Task :doNothingDebug"
      )
  }

  @Test
  fun neapolitan() {
    val buildScript = File(javaClass.classLoader.getResource("neapolitan.groovy").path)
    buildFile.writeBytes(buildScript.readBytes())

    val buildResult = GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
      .withArguments("tasks", "uploadStrawberryDebugAssets", "--stacktrace")
      .forwardOutput()
      .build()

    assertThat(buildResult.output)
      .contains("Android Github Release Plugin",

        "createVanillaDebugGithubRelease",
        "createChocolateDebugGithubRelease",
        "createStrawberryDebugGithubRelease",

        "createVanillaReleaseGithubRelease",
        "createChocolateReleaseGithubRelease",
        "createStrawberryReleaseGithubRelease",

        "uploadVanillaDebugAsset0",
        "uploadVanillaDebugAsset1",
        "uploadVanillaDebugAssets",

        "uploadChocolateDebugAsset0",
        "uploadChocolateDebugAssets",

        "uploadStrawberryDebugAsset0",
        "uploadStrawberryDebugAssets",

        "uploadVanillaReleaseAsset0",
        "uploadVanillaReleaseAssets",

        "uploadChocolateReleaseAssets",

        "uploadStrawberryReleaseAssets")
  }

  @Test
  fun rockyRoad() {
    val buildScript = File(javaClass.classLoader.getResource("rocky_road.groovy").path)
    buildFile.writeBytes(buildScript.readBytes())

    val buildResult = GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
      .withArguments("tasks", "uploadFudgePecanReleaseAssets", "--stacktrace")
      .forwardOutput()
      .build()

    assertThat(buildResult.output)
      .contains("Android Github Release Plugin",

        "createChocolatePeanutDebugGithubRelease",
        "uploadChocolatePeanutDebugAsset0",
        "uploadChocolatePeanutDebugAssets",

        "createChocolatePecanDebugGithubRelease",
        "uploadChocolatePecanDebugAsset0",
        "uploadChocolatePecanDebugAssets",

        "createChocolatePeanutReleaseGithubRelease",
        "uploadChocolatePeanutReleaseAsset0",
        "uploadChocolatePeanutReleaseAssets",

        "createChocolatePecanReleaseGithubRelease",
        "uploadChocolatePecanReleaseAsset0",
        "uploadChocolatePecanReleaseAssets",

        "createFudgePecanDebugGithubRelease",
        "uploadFudgePecanDebugAsset0",
        "uploadFudgePecanDebugAssets",

        "createFudgePecanReleaseGithubRelease",
        "uploadFudgePecanReleaseAsset0",
        "uploadFudgePecanReleaseAssets")
  }

  private fun systemProperty(): ReadOnlyProperty<Any, String> {
    return object : ReadOnlyProperty<Any, String> {
      override fun getValue(thisRef: Any, property: KProperty<*>): String {
        val prop = System.getProperty(property.name)
        assumeNotNull(prop)
        return prop
      }
    }
  }
}