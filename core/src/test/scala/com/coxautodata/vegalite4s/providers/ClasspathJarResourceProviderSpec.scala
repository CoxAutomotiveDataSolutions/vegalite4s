package com.coxautodata.vegalite4s.providers

import java.util.Base64

import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

class ClasspathJarResourceProviderSpec extends FunSpec with Matchers {

  it("Load vega dependencies from the classpath") {

    val fromClasspath =
      ClasspathJarResourceProvider("3").getJavascriptLibraryURLs
        .map(_.split(',').last)
        .map(b => new String(Base64.getDecoder.decode(b), "UTF-8"))

    val fromUrl =
      JsdelivrProvider("3.3.1", "2.6.0", "3.29.1").getJavascriptLibraryURLs
        .map(Source.fromURL(_, "UTF-8").mkString)

    fromClasspath should contain theSameElementsInOrderAs fromUrl
  }

}
