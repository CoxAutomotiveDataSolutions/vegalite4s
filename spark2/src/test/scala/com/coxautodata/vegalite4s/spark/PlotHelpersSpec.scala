package com.coxautodata.vegalite4s.spark

import java.sql.{Date, Timestamp}

import com.coxautodata.vegalite4s.VegaLite
import com.coxautodata.arrow._
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

  it("Produce a histogram plot") {
    val spark = SparkSession.builder().master("local[1]").getOrCreate()
    import spark.implicits._

    val data: DataFrame =
      (97 to 122).map(i => TestRecord(i.toChar.toString, i)).toDF()

    val plot = VegaLite()
      .hist(data)

    plot.toJson(_.spaces2) should be(
      """|{
         |  "$schema" : "https://vega.github.io/schema/vega-lite/v3.json",
         |  "data" : {
         |    "values" : [
         |      {
         |        "b" : 97
         |      },
         |      {
         |        "b" : 98
         |      },
         |      {
         |        "b" : 99
         |      },
         |      {
         |        "b" : 100
         |      },
         |      {
         |        "b" : 101
         |      },
         |      {
         |        "b" : 102
         |      },
         |      {
         |        "b" : 103
         |      },
         |      {
         |        "b" : 104
         |      },
         |      {
         |        "b" : 105
         |      },
         |      {
         |        "b" : 106
         |      },
         |      {
         |        "b" : 107
         |      },
         |      {
         |        "b" : 108
         |      },
         |      {
         |        "b" : 109
         |      },
         |      {
         |        "b" : 110
         |      },
         |      {
         |        "b" : 111
         |      },
         |      {
         |        "b" : 112
         |      },
         |      {
         |        "b" : 113
         |      },
         |      {
         |        "b" : 114
         |      },
         |      {
         |        "b" : 115
         |      },
         |      {
         |        "b" : 116
         |      },
         |      {
         |        "b" : 117
         |      },
         |      {
         |        "b" : 118
         |      },
         |      {
         |        "b" : 119
         |      },
         |      {
         |        "b" : 120
         |      },
         |      {
         |        "b" : 121
         |      },
         |      {
         |        "b" : 122
         |      }
         |    ]
         |  },
         |  "repeat" : [
         |    "b"
         |  ],
         |  "columns" : 3,
         |  "spec" : {
         |    "mark" : "bar",
         |    "encoding" : {
         |      "x" : {
         |        "field" : {
         |          "repeat" : "repeat"
         |        },
         |        "type" : "quantitative",
         |        "bin" : {
         |          "maxbins" : 50
         |        }
         |      },
         |      "y" : {
         |        "aggregate" : "count",
         |        "type" : "quantitative"
         |      }
         |    }
         |  }
         |}""".stripMargin)
  }

  it("Produce a scatter plot") {
    val spark = SparkSession.builder().master("local[1]").getOrCreate()
    import spark.implicits._

    val data: DataFrame =
      (97 to 122).map(i => TestScatterRecord(i, i*i)).toDF()

    val plot = VegaLite()
      .scatter(data)

    plot.toJson(_.spaces2) should be(
      """{
        |  "$schema" : "https://vega.github.io/schema/vega-lite/v3.json",
        |  "data" : {
        |    "values" : [
        |      {
        |        "a" : 97,
        |        "b" : 9409
        |      },
        |      {
        |        "a" : 98,
        |        "b" : 9604
        |      },
        |      {
        |        "a" : 99,
        |        "b" : 9801
        |      },
        |      {
        |        "a" : 100,
        |        "b" : 10000
        |      },
        |      {
        |        "a" : 101,
        |        "b" : 10201
        |      },
        |      {
        |        "a" : 102,
        |        "b" : 10404
        |      },
        |      {
        |        "a" : 103,
        |        "b" : 10609
        |      },
        |      {
        |        "a" : 104,
        |        "b" : 10816
        |      },
        |      {
        |        "a" : 105,
        |        "b" : 11025
        |      },
        |      {
        |        "a" : 106,
        |        "b" : 11236
        |      },
        |      {
        |        "a" : 107,
        |        "b" : 11449
        |      },
        |      {
        |        "a" : 108,
        |        "b" : 11664
        |      },
        |      {
        |        "a" : 109,
        |        "b" : 11881
        |      },
        |      {
        |        "a" : 110,
        |        "b" : 12100
        |      },
        |      {
        |        "a" : 111,
        |        "b" : 12321
        |      },
        |      {
        |        "a" : 112,
        |        "b" : 12544
        |      },
        |      {
        |        "a" : 113,
        |        "b" : 12769
        |      },
        |      {
        |        "a" : 114,
        |        "b" : 12996
        |      },
        |      {
        |        "a" : 115,
        |        "b" : 13225
        |      },
        |      {
        |        "a" : 116,
        |        "b" : 13456
        |      },
        |      {
        |        "a" : 117,
        |        "b" : 13689
        |      },
        |      {
        |        "a" : 118,
        |        "b" : 13924
        |      },
        |      {
        |        "a" : 119,
        |        "b" : 14161
        |      },
        |      {
        |        "a" : 120,
        |        "b" : 14400
        |      },
        |      {
        |        "a" : 121,
        |        "b" : 14641
        |      },
        |      {
        |        "a" : 122,
        |        "b" : 14884
        |      }
        |    ]
        |  },
        |  "repeat" : {
        |    "row" : [
        |      "a",
        |      "b"
        |    ],
        |    "column" : [
        |      "a",
        |      "b"
        |    ]
        |  },
        |  "spec" : {
        |    "mark" : "point",
        |    "encoding" : {
        |      "x" : {
        |        "field" : {
        |          "repeat" : "column"
        |        },
        |        "type" : "quantitative"
        |      },
        |      "y" : {
        |        "field" : {
        |          "repeat" : "row"
        |        },
        |        "type" : "quantitative"
        |      }
        |    }
        |  }
        |}""".stripMargin)
  }


  it("arrow hack") {
    val spark = SparkSession.builder().master("local[1]").getOrCreate()
    import spark.implicits._

    val data: DataFrame =
      (97 to 122).map(i => TestRecord(i.toChar.toString, i)).toDF()


    data.write.mode("overwrite").arrow("/tmp/arrowout")


    spark.stop()

  }

}

case class TestRecord(a: String, b: Int)

case class TestScatterRecord(a: Int, b: Int)

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
