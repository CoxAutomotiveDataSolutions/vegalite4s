package com.coxautodata.vegalite4s.spark

import com.coxautodata.vegalite4s.PlotHelpers._
import com.coxautodata.vegalite4s.{SpecConstruct, VegaLite}
import org.apache.spark.sql.types.{NumericType, StructField, StructType}
import org.apache.spark.sql.{Dataset, SparkSession}

object PlotHelpers {

  implicit class VegaLiteSparkHelpers(plot: VegaLite) {

    /**
      * Produce a matrix of histogram plots, used for quickly visualising
      * numerical columns in a plot.
      * All non-numerical columns will be ignored
      *
      * @param ds                Dataset to visualise
      * @param maxBins           Maximum number of bins to use when producing histogram
      * @param plotMatrixColumns Number of columns of plots to use
      * @param includeColumns    Columns from the Dataset to use. Uses all by default or when set to empty
      * @return
      */
    def hist(ds: Dataset[_], maxBins: Int = 50, plotMatrixColumns: Int = 3, includeColumns: Seq[String] = Seq.empty): VegaLite = {

      val numericColumns = filterNumericColumns(ds.schema).map(_.name)
      val plotColumns = if (includeColumns.isEmpty) numericColumns else numericColumns.filter(includeColumns.contains)

      val df = ds.toDF().select(numericColumns.map(ds.apply): _*)
      plot
        .withData(df)
        .withObject(
          s"""{
             |  "repeat": [${plotColumns.mkString("\"", "\", \"", "\"")}],
             |  "columns": $plotMatrixColumns,
             |  "spec": {
             |      "mark": "bar",
             |      "encoding": {
             |        "x": {"field": {"repeat": "repeat"},"type": "quantitative", "bin": {"maxbins": $maxBins}},
             |        "y": {
             |          "aggregate": "count",
             |          "type": "quantitative"
             |        }
             |      }
             |   }
             |}
          """.stripMargin
        )
    }

    /**
      * Produce a matrix of scatter plots, used for quickly visualising
      * correlations in a plot.
      * All non-numerical columns will be ignored
      *
      * @param ds                Dataset to visualise
      * @param includeColumns    Columns from the Dataset to use. Uses all by default or when set to empty
      * @return
      */
    def scatter(ds: Dataset[_], includeColumns: Seq[String] = Seq.empty): VegaLite = {

      val numericColumns = filterNumericColumns(ds.schema).map(_.name)
      val plotColumns = if (includeColumns.isEmpty) numericColumns else numericColumns.filter(includeColumns.contains)

      val df = ds.toDF().select(numericColumns.map(ds.apply): _*)
      plot
        .withData(df)
        .withObject(
          s"""{
             |  "repeat": {
             |    "row": [${plotColumns.mkString("\"", "\", \"", "\"")}],
             |    "column": [${plotColumns.mkString("\"", "\", \"", "\"")}]
             |  },
             |  "spec": {
             |      "mark": "point",
             |      "encoding": {
             |        "x": {"field": {"repeat": "column"},"type": "quantitative"},
             |        "y": {"field": {"repeat": "row"},"type": "quantitative" }
             |      }
             |   }
             |}
          """.stripMargin
        )
    }

  }

  implicit class SpecConstructSparkHelpers[T](spec: SpecConstruct[T]) {

    /**
      * Add a set of dataset to the plot.
      * Data is added under the `values` field in the `data` object
      * on the plot, and Dataset column names are used as VegaLite column names.
      * Spark DAG execution is not triggered until the plot is rendered.
      * Dataset is sampled if the record size is over the provided record limit.
      *
      * @param ds    Dataset to add
      */
    def withData(ds: Dataset[_]): T = {
      def toSeqMap: Seq[Map[String, Any]] = {
        val limit = getCollectLimit(ds.sparkSession)
        val count = ds.count()
        val arr =
          if (count <= limit) ds.toDF().collect()
          else
            ds.toDF()
              .sample(withReplacement = false, limit / count.toDouble)
              .collect()
        arr.toSeq.map(r => r.schema.toList.map(_.name).zip(r.toSeq).toMap)
      }

      spec.withData(toSeqMap)
    }

  }

  private def getCollectLimit(spark: SparkSession): Long = {
    val limitEnabled = spark.conf.getOption(SPARK_DATASET_LIMIT).map(_.toBoolean).getOrElse(SPARK_DATASET_LIMIT_DEFAULT)
    if (limitEnabled) {
      spark.conf.getOption(SPARK_DATASET_MAX_RECORDS).map(_.toLong).getOrElse(SPARK_DATASET_MAX_RECORDS_DEFAULT)
    }
    else -1
  }

  private def filterNumericColumns(schema: StructType): Seq[StructField] = {
    schema.fields.filter(_.dataType.isInstanceOf[NumericType])
  }

}
