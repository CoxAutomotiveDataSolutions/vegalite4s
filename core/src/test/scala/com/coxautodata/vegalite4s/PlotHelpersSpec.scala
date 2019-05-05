package com.coxautodata.vegalite4s

import java.sql.{Date, Timestamp}
import io.circe._
import com.coxautodata.vegalite4s.PlotHelpers._
import org.scalatest.{FunSpec, Matchers}
import java.math.{BigDecimal => JBigDecimal, BigInteger => JBigInteger}

class PlotHelpersSpec extends FunSpec with Matchers {

  it("add a title to a plot") {

    val plot = VegaLite()
      .withObject(
        """
          |{
          |  "description": "A simple bar chart with embedded data.",
          |  "mark": "bar",
          |  "encoding": {
          |    "x": {"field": "a", "type": "ordinal"},
          |    "y": {"field": "b", "type": "quantitative"}
          |  }
          |}
        """.stripMargin)
      .withTitle("example")

    plot.toJson(_.spaces2) should be(
      """{
        |  "$schema" : "https://vega.github.io/schema/vega-lite/v3.json",
        |  "description" : "A simple bar chart with embedded data.",
        |  "mark" : "bar",
        |  "encoding" : {
        |    "x" : {
        |      "field" : "a",
        |      "type" : "ordinal"
        |    },
        |    "y" : {
        |      "field" : "b",
        |      "type" : "quantitative"
        |    }
        |  },
        |  "title" : "example"
        |}""".stripMargin)

  }

  it("add data to a plot") {

    val plot = VegaLite()
      .withObject(
        """
          |{
          |  "description": "A simple bar chart with embedded data.",
          |  "mark": "bar",
          |  "encoding": {
          |    "x": {"field": "a", "type": "ordinal"},
          |    "y": {"field": "b", "type": "quantitative"}
          |  }
          |}
        """.stripMargin)
      .withData(
        Seq(
          Map("a" -> 1, "b" -> "one"),
          Map("a" -> 2, "b" -> "two"),
          Map("a" -> 3, "b" -> "three"),
          Map("a" -> 4, "b" -> "four")
        ))

    plot.toJson(_.spaces2) should be(
      """{
        |  "$schema" : "https://vega.github.io/schema/vega-lite/v3.json",
        |  "description" : "A simple bar chart with embedded data.",
        |  "mark" : "bar",
        |  "encoding" : {
        |    "x" : {
        |      "field" : "a",
        |      "type" : "ordinal"
        |    },
        |    "y" : {
        |      "field" : "b",
        |      "type" : "quantitative"
        |    }
        |  },
        |  "data" : {
        |    "values" : [
        |      {
        |        "a" : 1,
        |        "b" : "one"
        |      },
        |      {
        |        "a" : 2,
        |        "b" : "two"
        |      },
        |      {
        |        "a" : 3,
        |        "b" : "three"
        |      },
        |      {
        |        "a" : 4,
        |        "b" : "four"
        |      }
        |    ]
        |  }
        |}""".stripMargin)

  }

  it("add width to a plot") {

    val plot = VegaLite()
      .withObject(
        """
          |{
          |  "description": "A simple bar chart with embedded data.",
          |  "mark": "bar",
          |  "encoding": {
          |    "x": {"field": "a", "type": "ordinal"},
          |    "y": {"field": "b", "type": "quantitative"}
          |  }
          |}
        """.stripMargin)
      .withWidth(200)

    plot.toJson(_.spaces2) should be(
      """{
        |  "$schema" : "https://vega.github.io/schema/vega-lite/v3.json",
        |  "description" : "A simple bar chart with embedded data.",
        |  "mark" : "bar",
        |  "encoding" : {
        |    "x" : {
        |      "field" : "a",
        |      "type" : "ordinal"
        |    },
        |    "y" : {
        |      "field" : "b",
        |      "type" : "quantitative"
        |    }
        |  },
        |  "width" : 200
        |}""".stripMargin)

  }

  it("add height to a plot") {

    val plot = VegaLite()
      .withObject(
        """
          |{
          |  "description": "A simple bar chart with embedded data.",
          |  "mark": "bar",
          |  "encoding": {
          |    "x": {"field": "a", "type": "ordinal"},
          |    "y": {"field": "b", "type": "quantitative"}
          |  }
          |}
        """.stripMargin)
      .withHeight(200)

    plot.toJson(_.spaces2) should be(
      """{
        |  "$schema" : "https://vega.github.io/schema/vega-lite/v3.json",
        |  "description" : "A simple bar chart with embedded data.",
        |  "mark" : "bar",
        |  "encoding" : {
        |    "x" : {
        |      "field" : "a",
        |      "type" : "ordinal"
        |    },
        |    "y" : {
        |      "field" : "b",
        |      "type" : "quantitative"
        |    }
        |  },
        |  "height" : 200
        |}""".stripMargin)

  }

  describe("withLayer") {

    it("should add layers to spec") {

      val plot = VegaLite()
        .withLayer {
          _.withObject(
            """
              |{
              |  "data": {
              |    "url": "data/us-10m.json",
              |    "format": {
              |      "type": "topojson",
              |      "feature": "states"
              |    }
              |  },
              |  "projection": {
              |    "type": "albersUsa"
              |  },
              |  "mark": {
              |    "type": "geoshape",
              |    "fill": "lightgray",
              |    "stroke": "white"
              |  }
              |}
            """.stripMargin
          )
        }
        .withLayer {
          _.withObject(
            """
              |    {
              |      "projection": {
              |        "type": "albersUsa"
              |      },
              |      "mark": "circle",
              |      "encoding": {
              |        "longitude": {
              |          "field": "longitude",
              |          "type": "quantitative"
              |        },
              |        "latitude": {
              |          "field": "latitude",
              |          "type": "quantitative"
              |        },
              |        "size": {"value": 10},
              |        "color": {"value": "steelblue"}
              |      }
              |    }
            """.stripMargin
          )
            .withData(
              Seq(
                Map("a" -> 1, "b" -> "one"),
                Map("a" -> 2, "b" -> "two"),
                Map("a" -> 3, "b" -> "three"),
                Map("a" -> 4, "b" -> "four")
              ))
        }
        .withTitle("Layered Plot")

      plot.toJson(_.spaces2) should be(
        """|{
           |  "$schema" : "https://vega.github.io/schema/vega-lite/v3.json",
           |  "layer" : [
           |    {
           |      "data" : {
           |        "url" : "data/us-10m.json",
           |        "format" : {
           |          "type" : "topojson",
           |          "feature" : "states"
           |        }
           |      },
           |      "projection" : {
           |        "type" : "albersUsa"
           |      },
           |      "mark" : {
           |        "type" : "geoshape",
           |        "fill" : "lightgray",
           |        "stroke" : "white"
           |      }
           |    },
           |    {
           |      "projection" : {
           |        "type" : "albersUsa"
           |      },
           |      "mark" : "circle",
           |      "encoding" : {
           |        "longitude" : {
           |          "field" : "longitude",
           |          "type" : "quantitative"
           |        },
           |        "latitude" : {
           |          "field" : "latitude",
           |          "type" : "quantitative"
           |        },
           |        "size" : {
           |          "value" : 10
           |        },
           |        "color" : {
           |          "value" : "steelblue"
           |        }
           |      },
           |      "data" : {
           |        "values" : [
           |          {
           |            "a" : 1,
           |            "b" : "one"
           |          },
           |          {
           |            "a" : 2,
           |            "b" : "two"
           |          },
           |          {
           |            "a" : 3,
           |            "b" : "three"
           |          },
           |          {
           |            "a" : 4,
           |            "b" : "four"
           |          }
           |        ]
           |      }
           |    }
           |  ],
           |  "title" : "Layered Plot"
           |}""".stripMargin)
    }

    it("show throw an exception if layer is not an array") {

      intercept[ParsingFailure](
        VegaLite()
          .withField("layer", Json.fromString("test"))
          .withLayer(_.withField("field", Json.fromString("field")))
          .toJson
      ).message should be("Layer field must an array type")

    }

  }

  describe("anyEncoder") {

    it("encode Int") {
      anyEncoder(1).noSpaces should be("1")
    }

    it("encode Long") {
      anyEncoder(1L).noSpaces should be("1")
    }

    it("encode Short") {
      anyEncoder(1.toShort).noSpaces should be("1")
    }

    it("encode Boolean") {
      anyEncoder(true).noSpaces should be("true")
    }

    it("encode String") {
      anyEncoder("string").noSpaces should be("\"string\"")
    }

    it("encode BigInt") {
      anyEncoder(BigInt("123456789123456789")).noSpaces should be("123456789123456789")
    }

    it("encode Java BigInt") {
      anyEncoder(new JBigInteger("123456789123456789")).noSpaces should be("123456789123456789")
    }

    it("encode double") {
      anyEncoder(1.3d).noSpaces should be("1.3")
    }

    it("encode float") {
      anyEncoder(1.3f).noSpaces should be("1.3")
    }

    it("encode BigDecimal") {
      anyEncoder(BigDecimal("123456789123456789.1")).noSpaces should be("123456789123456789.1")
    }

    it("encode Java BigDecimal") {
      anyEncoder(new JBigDecimal("123456789123456789.1")).noSpaces should be("123456789123456789.1")
    }

    it("encode Timestamp") {
      anyEncoder(new Timestamp(1552306550229L)).noSpaces should be("\"2019-03-11T12:15:50.229\"")
    }

    it("encode Date") {
      anyEncoder(new Date(1552306550229L)).noSpaces should be("\"2019-03-11\"")
    }

    it("encode null") {
      anyEncoder(null).noSpaces should be("null")
    }

    it("encode TestObject") {
      anyEncoder(TestObject).noSpaces should be("\"TestValue\"")
    }

  }


}

object TestObject {
  override def toString: String = "TestValue"
}
