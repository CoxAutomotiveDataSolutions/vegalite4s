package com.coxautodata.arrow

import org.apache.hadoop.mapreduce.TaskAttemptContext
import org.apache.spark.sql.execution.datasources.OutputWriterFactory
import org.apache.spark.sql.types.StructType

class ArrowOutputWriterFactor(maxRowsPerBatch: Long, timeZoneId: String) extends OutputWriterFactory {
  override def getFileExtension(context: TaskAttemptContext): String = ".arrow"

  override def newInstance(path: String, dataSchema: StructType, context: TaskAttemptContext): ArrowOutputWriter = {
    new ArrowOutputWriter(path, context, dataSchema, maxRowsPerBatch, timeZoneId)
  }
}
