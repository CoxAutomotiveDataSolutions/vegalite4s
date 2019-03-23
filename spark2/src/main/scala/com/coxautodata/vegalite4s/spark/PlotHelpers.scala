package com.coxautodata.vegalite4s.spark

import com.coxautodata.vegalite4s.PlotHelpers._
import com.coxautodata.vegalite4s.{SpecConstruct, VegaLite}
import org.apache.spark.sql.Dataset

object PlotHelpers {

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
