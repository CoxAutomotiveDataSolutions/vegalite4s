package com.coxautodata.vegalite4s.spark.filestore

import org.apache.hadoop.fs.Path
import org.apache.spark.sql.SparkSession



trait StaticFileStore {

  def staticStoreExists(sparkSession: SparkSession): Boolean

  def generateOutputPath(): Path

  def getStaticURL(arrowFile: Path): String
}
