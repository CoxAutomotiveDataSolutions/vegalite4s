package com.coxautodata.vegalite4s.spark

import com.coxautodata.vegalite4s.VegaLite
import com.coxautodata.vegalite4s.spark.PlotHelpers._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.{FunSpec, Matchers}

class PlotHelpersSpec extends FunSpec with Matchers {

  it("Add dataframe data to a VegaLite plot") {
    val spark = SparkSession.builder().master("local[1]").getOrCreate()
    import spark.implicits._

    val data: DataFrame =
      (97 to 122).map(i => TestRecord(i.toChar.toString, i)).toDF()

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
      .withData(data)

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
        |        "a" : "a",
        |        "b" : 97
        |      },
        |      {
        |        "a" : "b",
        |        "b" : 98
        |      },
        |      {
        |        "a" : "c",
        |        "b" : 99
        |      },
        |      {
        |        "a" : "d",
        |        "b" : 100
        |      },
        |      {
        |        "a" : "e",
        |        "b" : 101
        |      },
        |      {
        |        "a" : "f",
        |        "b" : 102
        |      },
        |      {
        |        "a" : "g",
        |        "b" : 103
        |      },
        |      {
        |        "a" : "h",
        |        "b" : 104
        |      },
        |      {
        |        "a" : "i",
        |        "b" : 105
        |      },
        |      {
        |        "a" : "j",
        |        "b" : 106
        |      },
        |      {
        |        "a" : "k",
        |        "b" : 107
        |      },
        |      {
        |        "a" : "l",
        |        "b" : 108
        |      },
        |      {
        |        "a" : "m",
        |        "b" : 109
        |      },
        |      {
        |        "a" : "n",
        |        "b" : 110
        |      },
        |      {
        |        "a" : "o",
        |        "b" : 111
        |      },
        |      {
        |        "a" : "p",
        |        "b" : 112
        |      },
        |      {
        |        "a" : "q",
        |        "b" : 113
        |      },
        |      {
        |        "a" : "r",
        |        "b" : 114
        |      },
        |      {
        |        "a" : "s",
        |        "b" : 115
        |      },
        |      {
        |        "a" : "t",
        |        "b" : 116
        |      },
        |      {
        |        "a" : "u",
        |        "b" : 117
        |      },
        |      {
        |        "a" : "v",
        |        "b" : 118
        |      },
        |      {
        |        "a" : "w",
        |        "b" : 119
        |      },
        |      {
        |        "a" : "x",
        |        "b" : 120
        |      },
        |      {
        |        "a" : "y",
        |        "b" : 121
        |      },
        |      {
        |        "a" : "z",
        |        "b" : 122
        |      }
        |    ]
        |  }
        |}""".stripMargin
    )

    spark.stop()

  }

}

case class TestRecord(a: String, b: Int)
