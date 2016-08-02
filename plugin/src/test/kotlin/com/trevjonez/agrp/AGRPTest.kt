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

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * @author TrevJonez
 */
class AGRPTest {
  @Rule @JvmField var testProjectDir = TemporaryFolder()

  lateinit var buildFile: File
  lateinit var propertiesFile: File

  @Before
  fun setUp() {
    buildFile = testProjectDir.newFile("build.gradle")
    propertiesFile = testProjectDir.newFile("local.properties")
    propertiesFile.writeText("sdk.dir=/Sources/android_sdk_mac_x86")
  }

  @Test
  @Throws(Exception::class)
  fun applyPlugin() {
    val project = ProjectBuilder.builder().build()

    project.pluginManager.apply("com.android.application")
    project.pluginManager.apply("com.trevjonez.AndroidGithubReleasePlugin")

    val baseExtension = project.extensions.findByName("AndroidGithubRelease") as ExtensionAware
    assertThat(baseExtension)
            .isNotNull()
            .isInstanceOf(AgrpConfigExtension::class.java)
  }

  @Test
  fun applyPlugin2() {
    val buildScript = File(javaClass.classLoader.getResource("vanilla.gradle").path)
    buildFile.writeBytes(buildScript.readBytes())

    val buildResult = GradleRunner.create()
            .withGradleVersion("2.13")
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments("--stacktrace", "--info")
            .build()

    print(buildResult.output)
  }
}