package com.coxautodata.vegalite4s

package object spark {

  val SPARK_DATASET_LIMIT = "spark.vegalite4s.limitCollect"
  val SPARK_DATASET_LIMIT_DEFAULT: Boolean = true

  val SPARK_DATASET_MAX_RECORDS = "spark.vegalite4s.maxRowsToCollect"
  val SPARK_DATASET_MAX_RECORDS_DEFAULT: Long = 10000

  val SPARK_ATTEMPT_TO_STORE_AS_STATIC_ARROW_FILE = "spark.vegalite4s.tryStoreAsArrow"
  val SPARK_ATTEMPT_TO_STORE_AS_STATIC_ARROW_FILE_DEFAULT: Boolean = true

}
