package com.coxautodata.vegalite4s

import com.coxautodata.vegalite4s.providers.{JsdelivrProvider, VegaProvider}
import io.circe.{Json, JsonObject}
import org.scalatest.{FunSpec, Matchers}

class VegaSpec extends FunSpec with Matchers {

  describe("withObject") {

    it("unique fields") {
      val plot = Vega(VegaProvider(JsdelivrProvider("3", "2", "3"), "3"))
        .withObject("""{ "a": "a" }""")
        .withObject(JsonObject("b" -> Json.fromString("b")))

      plot.toJson(_.spaces2) should be(
        """{
          |  "$schema" : "https://vega.github.io/schema/vega/v3.json",
          |  "a" : "a",
          |  "b" : "b"
          |}""".stripMargin
      )

    }
  }

}
