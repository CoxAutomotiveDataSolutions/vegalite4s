package com.coxautodata.arrow

import org.apache.hadoop.fs.FileStatus
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.datasources.{FileFormat, OutputWriterFactory}
import org.apache.spark.sql.types.StructType

private[arrow] class DefaultSource extends FileFormat {
  override def inferSchema(sparkSession: SparkSession, options: Map[String, String], files: Seq[FileStatus]): Option[StructType] =
    throw new UnsupportedOperationException(s"Read not yet supported")

  override def prepareWrite(sparkSession: SparkSession, job: Job, options: Map[String, String], dataSchema: StructType): OutputWriterFactory = {

    //https://spark.apache.org/docs/latest/sql-pyspark-pandas-with-arrow.html#timestamp-with-time-zone-semantics
    val maxRecordsPerBatch = options.getOrElse("maxRecordsPerBatch", "10000").toInt
    val timeZoneId = options.getOrElse("timeZoneId", "UTC")
    new ArrowOutputWriterFactor(maxRecordsPerBatch, timeZoneId)

  }

}
