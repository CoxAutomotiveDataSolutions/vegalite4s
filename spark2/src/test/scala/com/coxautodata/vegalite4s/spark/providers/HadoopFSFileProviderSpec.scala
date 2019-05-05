package com.coxautodata.vegalite4s.spark.providers

import java.net.URL
import java.nio.file.{Files, StandardCopyOption}
import java.util.Base64

import com.coxautodata.vegalite4s.providers.JsdelivrProvider
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.SparkSession
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

class HadoopFSFileProviderSpec extends FunSpec with Matchers {

  it("Load vega dependencies from hadoop paths") {

    val spark = SparkSession.builder().master("local[1]").getOrCreate()

    val jsProvider = JsdelivrProvider("3.3.1", "2.6.0", "3.29.1")

    val fromLocalFiles = {
      val paths = jsProvider.getJavascriptLibraryURLs
        .map { url =>
          val in = new URL(url).openStream()
          val tmp =
            Files.createTempFile(url.split('/').last.split('@').head, ".min.js")
          Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING)
          new Path(s"file://${tmp.toUri.getPath}")
        }
      assert(paths.length == 3)
      HadoopFSFileProvider(spark, paths(0), paths(1), paths(2))
    }.getJavascriptLibraryURLs
      .map(_.split(',').last)
      .map(b => new String(Base64.getDecoder.decode(b), "UTF-8"))

    val fromUrl =
      jsProvider.getJavascriptLibraryURLs
        .map(Source.fromURL(_, "UTF-8").mkString)

    fromLocalFiles should contain theSameElementsInOrderAs fromUrl

    spark.stop()
  }

}
