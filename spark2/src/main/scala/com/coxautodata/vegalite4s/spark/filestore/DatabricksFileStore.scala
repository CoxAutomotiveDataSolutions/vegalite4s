package com.coxautodata.vegalite4s.spark.filestore

import java.util.UUID

import org.apache.hadoop.fs.Path
import org.apache.spark.sql.SparkSession

import scala.util.Try


object DatabricksFileStore extends StaticFileStore {

  val root: Path = new Path("dbfs:/FileStore")

  override def staticStoreExists(sparkSession: SparkSession): Boolean = Try {
    root
      .getFileSystem(sparkSession.sparkContext.hadoopConfiguration)
      .exists(root)
  }.getOrElse(false)

  override def generateOutputPath(): Path = new Path(root, s"vegalite4s/${UUID.randomUUID()}")

  override def getStaticURL(arrowFile: Path): String = new Path("files", root.toUri.relativize(arrowFile.toUri).getPath).toString
}
