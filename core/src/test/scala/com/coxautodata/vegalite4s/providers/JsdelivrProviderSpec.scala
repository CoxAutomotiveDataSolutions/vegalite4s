package com.coxautodata.vegalite4s.providers

import org.scalatest.{FunSpec, Matchers}

class JsdelivrProviderSpec extends FunSpec with Matchers {

  it("Generate URLs to link to Jsdeliver files") {

    JsdelivrProvider("3", "2", "3").getJavascriptLibraryURLs should be(
      Seq(
        "https://cdn.jsdelivr.net/npm/vega@3",
        "https://cdn.jsdelivr.net/npm/vega-lite@2",
        "https://cdn.jsdelivr.net/npm/vega-embed@3"
      )
    )
  }

}
