package com.coxautodata.vegalite4s

import com.coxautodata.vegalite4s.providers.JsdelivrProvider
import io.circe.{Json, JsonObject}
import org.scalatest.{FunSpec, Matchers}

class VegaLiteSpec extends FunSpec with Matchers {

  describe("withObject") {

    it("unique fields") {
      val plot = VegaLite(JsdelivrProvider("3", "2", "3"))
        .withObject("""{ "a": "a" }""")
        .withObject(JsonObject("b" -> Json.fromString("b")))

      plot.toJson should be(
        """{
          |  "$schema" : "https://vega.github.io/schema/vega-lite/v2.json",
          |  "a" : "a",
          |  "b" : "b"
          |}""".stripMargin
      )

    }

    it("overwritten fields") {
      val plot = VegaLite(JsdelivrProvider("3", "2", "3"))
        .withObject("""{ "a": "a" }""")
        .withObject(JsonObject("a" -> Json.fromString("aa")))
        .withObject(JsonObject("b" -> Json.fromString("b")))
        .withObject("""{ "b": "bb" }""")

      plot.toJson should be(
        """{
          |  "$schema" : "https://vega.github.io/schema/vega-lite/v2.json",
          |  "a" : "aa",
          |  "b" : "bb"
          |}""".stripMargin
      )

    }
  }

  describe("withField") {

    it("unique fields") {
      val plot = VegaLite(JsdelivrProvider("3", "2", "3"))
        .withField("a", "[ \"a\"]")
        .withField("b", Json.fromString("b"))

      plot.toJson should be(
        """{
          |  "$schema" : "https://vega.github.io/schema/vega-lite/v2.json",
          |  "a" : [
          |    "a"
          |  ],
          |  "b" : "b"
          |}""".stripMargin
      )

    }

    it("overwritten fields") {
      val plot = VegaLite(JsdelivrProvider("3", "2", "3"))
        .withField("a", "[ \"a\"]")
        .withField("b", Json.fromString("b"))
        .withField("a", Json.fromString("aa"))
        .withField("b", "[ \"bb\"]")

      plot.toJson should be(
        """{
          |  "$schema" : "https://vega.github.io/schema/vega-lite/v2.json",
          |  "a" : "aa",
          |  "b" : [
          |    "bb"
          |  ]
          |}""".stripMargin
      )

    }

  }

}
