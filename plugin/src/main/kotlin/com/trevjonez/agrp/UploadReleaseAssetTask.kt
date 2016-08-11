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

import okhttp3.RequestBody
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
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

/**
 * @author TrevJonez
 */
open class UploadReleaseAssetTask : DefaultTask() {
  lateinit var createTask: CreateReleaseTask
  lateinit var assetFile: File

  @TaskAction
  fun uploadAsset() {

    var contentType = URLConnection.getFileNameMap().getContentTypeFor(assetFile.name)

    if (assetFile.isDirectory) {
      val zipFile = File(assetFile.parent, "${assetFile.name}.zip")
      FileOutputStream(zipFile).use({ fos ->
        ZipOutputStream(fos).use({ zos ->
          Files.walkFileTree(assetFile.toPath(), object : SimpleFileVisitor<Path>() {
            @Throws(IOException::class)
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
              zos.putNextEntry(ZipEntry(assetFile.toPath().relativize(file).toString()))
              Files.copy(file, zos)
              zos.closeEntry()
              return FileVisitResult.CONTINUE
            }

            @Throws(IOException::class)
            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
              zos.putNextEntry(ZipEntry(assetFile.toPath().relativize(dir).toString() + "/"))
              zos.closeEntry()
              return FileVisitResult.CONTINUE
            }
          })
        })
      })
      assetFile = zipFile
      contentType = "application/zip"
    }

    if (contentType == null) {
      //Nothing was set lets try application/ ¯\_(ツ)_/¯
      contentType = "application/octet-stream"
    }

    val requestBody = RequestBody.create(null, assetFile)
    val uploadCall = createTask.releaseService().uploadReleaseAsset(createTask.response.upload_url!!.replace("{?name,label}", ""), assetFile.name, requestBody, contentType, "token ${createTask.accessToken()}")
    val uploadResult = uploadCall.execute()

    if (!uploadResult.isSuccessful) {
      throw GradleException("Asset \"${assetFile.name}\" upload failed: ${uploadResult.errorBody().string()}")
    }

    println("Release asset uploaded: ${assetFile.name}")
  }
}