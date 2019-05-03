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

import okhttp3.RequestBody
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLConnection
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

open class UploadReleaseAssetTask : AgrpTask() {
  lateinit var createTask: TaskProvider<CreateReleaseTask>

  @InputFile
  val assetFile: RegularFileProperty = project.objects.fileProperty()

  @TaskAction
  fun uploadAsset() {
    val rawFile = assetFile.asFile.get()
    val (contentType, uploadFile) = if (rawFile.isDirectory) {
      "application/zip" to File(rawFile.parent, "${rawFile.name}.zip").apply {
        ZipOutputStream(FileOutputStream(this)).use { zos ->
          Files.walkFileTree(rawFile.toPath(), object : SimpleFileVisitor<Path>() {
            @Throws(IOException::class)
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
              zos.putNextEntry(ZipEntry(rawFile.toPath().relativize(file).toString()))
              Files.copy(file, zos)
              zos.closeEntry()
              return FileVisitResult.CONTINUE
            }

            @Throws(IOException::class)
            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
              zos.putNextEntry(ZipEntry(rawFile.toPath().relativize(dir).toString() + "/"))
              zos.closeEntry()
              return FileVisitResult.CONTINUE
            }
          })
        }
      }
    } else {
      (URLConnection.getFileNameMap().getContentTypeFor(rawFile.name) ?: "application/octet-stream") to rawFile
    }

    val uploadUrl = createTask.get().response.upload_url.replace("{?name,label}", "")

    val uploadResult = config.releaseApi
        .uploadAsset(
            uploadUrl,
            uploadFile.name,
            "token ${config.accessToken}",
            contentType,
            RequestBody.create(null, uploadFile)
        )
        .execute()

    if (!uploadResult.isSuccessful) {
      project.logger.lifecycle("Outbound request URL on failed asset upload was: `$uploadUrl`")
      project.logger.lifecycle("Upload result failed with code: ${uploadResult.code()}")
      throw GradleException("Asset \"${uploadFile.name}\" upload failed: ${uploadResult.errorBody()!!.string()}")
    }

    project.logger.lifecycle("Release asset uploaded: ${uploadFile.name}")
  }
}