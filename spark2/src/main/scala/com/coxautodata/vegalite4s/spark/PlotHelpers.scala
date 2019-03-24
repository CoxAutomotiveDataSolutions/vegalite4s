package com.coxautodata.vegalite4s.spark

import com.coxautodata.vegalite4s.PlotHelpers._
import com.coxautodata.vegalite4s.{SpecConstruct, VegaLite}
import org.apache.spark.sql.Dataset

object PlotHelpers {

  implicit class VegaLiteSparkHelpers(plot: VegaLite) {

    /**
      * Produce a matrix of histogram plots, used for quickly visualising
      * numerical columns in a plot.
      * All non-numerical columns will be ignored
      *
      * @param ds                Dataset to visualise
      * @param limit             See [[SpecConstructSparkHelpers.withData]]
      * @param maxBins           Maximum number of bins to use when producing histogram
      * @param plotMatrixColumns Number of columns of plots to use
      * @param includeColumns    Columns from the Dataset to use. Uses all by default or when set to empty
      * @return
      */
    def hist(ds: Dataset[_], limit: Int = 10000, maxBins: Int = 50, plotMatrixColumns: Int = 3, includeColumns: Seq[String] = Seq.empty): VegaLite = {
      import org.apache.spark.sql.types.DataTypes._

      val numericTypes = Seq(IntegerType, DoubleType, FloatType, LongType, ShortType)
      val numericColumns = ds.schema.fields.filter(f => numericTypes.contains(f.dataType)).map(_.name)
      val plotColumns = if (includeColumns.isEmpty) numericColumns else numericColumns.filter(includeColumns.contains)

      val df = ds.toDF().select(numericColumns.map(ds.apply): _*)
      plot
        .withData(df, limit)
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
      * @param limit Maximum number of records to include
      */
    def withData(ds: Dataset[_], limit: Int = 10000): T = {
      def toSeqMap: Seq[Map[String, Any]] = {
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

}
