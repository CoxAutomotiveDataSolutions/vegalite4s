package com.coxautodata.vegalite4s.providers

import java.net.URL
import java.nio.file.{Files, StandardCopyOption}
import java.util.Base64

import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

class LocalFileProviderSpec extends FunSpec with Matchers {

  it("Load vega dependencies from local files") {

    val jsProvider = JsdelivrProvider("3.3.1", "2.6.0", "3.29.1")

    val fromLocalFiles = {
      val files = jsProvider.getJavascriptLibraryURLs
        .map { url =>
          val in = new URL(url).openStream()
          val tmp =
            Files.createTempFile(url.split('/').last.split('@').head, ".min.js")
          Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING)
          tmp.toString
        }
      assert(files.length == 3)
      LocalFileProvider("2", files(0), files(1), files(2))
    }.getJavascriptLibraryURLs
      .map(_.split(',').last)
      .map(b => new String(Base64.getDecoder.decode(b), "UTF-8"))

    val fromUrl =
      jsProvider.getJavascriptLibraryURLs
        .map(Source.fromURL(_, "UTF-8").mkString)

    fromLocalFiles should contain theSameElementsInOrderAs fromUrl
  }

}
