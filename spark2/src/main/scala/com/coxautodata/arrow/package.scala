package com.coxautodata

import org.apache.spark.sql.DataFrameWriter

package object arrow {

  implicit class ArrowDataFrameWriter[T](writer: DataFrameWriter[T]) {
    def arrow: String => Unit = writer.format("com.coxautodata.arrow").save
  }

}
