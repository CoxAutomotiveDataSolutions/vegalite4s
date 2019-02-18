package com.coxautodata.vegalite4s

import com.coxautodata.vegalite4s.PlotHelpers._
import org.scalatest.{FunSpec, Matchers}

class PlotHelpersSpec extends FunSpec with Matchers {

  it("add a title to a plot") {

    val plot = VegaLite()
      .withObject("""
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

    plot.toJson should be(
      """{
        |  "$schema" : "https://vega.github.io/schema/vega-lite/v2.json",
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
      .withObject("""
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

    plot.toJson should be(
      """{
        |  "$schema" : "https://vega.github.io/schema/vega-lite/v2.json",
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
      .withObject("""
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

    plot.toJson should be(
      """{
        |  "$schema" : "https://vega.github.io/schema/vega-lite/v2.json",
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
      .withObject("""
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

    plot.toJson should be(
      """{
        |  "$schema" : "https://vega.github.io/schema/vega-lite/v2.json",
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

}
