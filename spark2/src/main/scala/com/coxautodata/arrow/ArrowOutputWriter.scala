package com.coxautodata.arrow

import java.nio.channels.Channels

import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.dictionary.DictionaryProvider
import org.apache.arrow.vector.ipc.ArrowFileWriter
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.TaskAttemptContext
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.execution.arrow.{ArrowUtils, ArrowWriter}
import org.apache.spark.sql.execution.datasources.OutputWriter
import org.apache.spark.sql.types.StructType

class ArrowOutputWriter(path: String, context: TaskAttemptContext, schema: StructType, maxRowsPerBatch: Long, timeZoneId: String) extends OutputWriter {


  private val outputPath = new Path(path)
  private val outputStream = outputPath.getFileSystem(context.getConfiguration).create(outputPath)
  private val outputChannel = Channels.newChannel(outputStream)
  private val arrowSchema = ArrowUtils.toArrowSchema(schema, timeZoneId)
  private val vectorSchema = VectorSchemaRoot.create(arrowSchema, ArrowUtils.rootAllocator)
  private val rootWriter = ArrowWriter.create(vectorSchema)
  private val dictionaryProvider = new DictionaryProvider.MapDictionaryProvider()
  private val fileWriter = new ArrowFileWriter(vectorSchema, dictionaryProvider, outputChannel)
  private var count: Long = 0L

  //https://github.com/animeshtrivedi/blog/blob/master/post/2017-12-26-arrow.md

  private def commitBatch(): Unit = {
    rootWriter.finish()
    fileWriter.writeBatch()
    rootWriter.reset()
    count = 0
  }

  override def write(row: InternalRow): Unit = {
    rootWriter.write(row)
    count += 1
    if (count >= maxRowsPerBatch) commitBatch()
  }

  override def close(): Unit = {
    if (count != 0) commitBatch()
    outputChannel.close()
  }
}
