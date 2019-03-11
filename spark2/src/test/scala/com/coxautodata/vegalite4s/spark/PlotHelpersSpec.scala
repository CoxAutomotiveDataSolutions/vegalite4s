package com.coxautodata.vegalite4s.spark

import java.sql.{Date, Timestamp}

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

  it("add a dataset to the plot with all supported spark datatypes") {
    val spark = SparkSession.builder().master("local[1]").getOrCreate()
    import spark.implicits._

    val data = Seq(
      TestDatatypesRecord(
        Some("string"),
        Some(1),
        Some(100000000L),
        Some(1),
        Some(1.1),
        Some(true),
        Some(1f),
        Some(BigInt("123456789123456789")),
        Some(BigDecimal("123456789123456789.1")),
        Some(new Timestamp(1552306550229L)),
        Some(new Date(1552306550229L))),
      TestDatatypesRecord(None, None, None, None, None, None, None, None, None, None, None)
    ).toDS()

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
        |        "floatF" : 1.0,
        |        "stringF" : "string",
        |        "booleanF" : true,
        |        "dateF" : "2019-03-11",
        |        "bigdecimalF" : 123456789123456789.100000000000000000,
        |        "doubleF" : 1.1,
        |        "integerF" : 1,
        |        "timestampF" : "2019-03-11T12:15:50.229",
        |        "shortF" : 1,
        |        "bigintF" : 123456789123456789,
        |        "longF" : 100000000
        |      },
        |      {
        |        "floatF" : null,
        |        "stringF" : null,
        |        "booleanF" : null,
        |        "dateF" : null,
        |        "bigdecimalF" : null,
        |        "doubleF" : null,
        |        "integerF" : null,
        |        "timestampF" : null,
        |        "shortF" : null,
        |        "bigintF" : null,
        |        "longF" : null
        |      }
        |    ]
        |  }
        |}""".stripMargin)

  }

}

case class TestRecord(a: String, b: Int)

case class TestDatatypesRecord(stringF: Option[String],
                               integerF: Option[Int],
                               longF: Option[Long],
                               shortF: Option[Short],
                               doubleF: Option[Double],
                               booleanF: Option[Boolean],
                               floatF: Option[Float],
                               bigintF: Option[BigInt],
                               bigdecimalF: Option[BigDecimal],
                               timestampF: Option[Timestamp],
                               dateF: Option[Date])
