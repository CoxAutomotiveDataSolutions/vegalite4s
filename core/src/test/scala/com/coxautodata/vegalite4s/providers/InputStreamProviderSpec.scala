package com.coxautodata.vegalite4s.providers

import java.io.ByteArrayInputStream
import java.util.Base64

import org.scalatest.{FunSpec, Matchers}

class InputStreamProviderSpec extends FunSpec with Matchers {

  it("Read in a text stream and convert it into a base64 Javascript source") {

    val testString =
      """
        |Test
        |String
      """.stripMargin

    val url = new InputStreamProvider(
      new ByteArrayInputStream(testString.getBytes())
    ).getJavascriptLibraryURLs

    url should be(
      Seq("data:text/javascript;base64,ClRlc3QKU3RyaW5nCiAgICAgIA==")
    )

    url.head
      .split(',')
      .lastOption
      .map(v => new String(Base64.getDecoder.decode(v)))
      .get should be(testString)

  }

}
