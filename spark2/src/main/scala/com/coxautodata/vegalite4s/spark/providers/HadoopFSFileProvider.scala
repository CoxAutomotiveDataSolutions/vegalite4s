package com.coxautodata.vegalite4s.spark.providers

import com.coxautodata.vegalite4s.providers.{InputStreamProvider, LibraryProvider, VegaLiteProvider}
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.SparkSession

/**
  * A VegaLite dependency provider that takes the paths to the
  * Javascript files on a Hadoop Filesystem and embeds them in the
  * rendered plot using the [[InputStreamProvider]].
  */
object HadoopFSFileProvider {

  def apply(sparkSession: SparkSession,
            vegaPath: Path,
            vegaLitePath: Path,
            vegaEmbedPath: Path): LibraryProvider = {

    val streams = Seq(vegaPath, vegaLitePath, vegaEmbedPath)
      .map { p =>
        p.getFileSystem(sparkSession.sparkContext.hadoopConfiguration).open(p)
      }

    new InputStreamProvider(streams: _*)

  }

}
