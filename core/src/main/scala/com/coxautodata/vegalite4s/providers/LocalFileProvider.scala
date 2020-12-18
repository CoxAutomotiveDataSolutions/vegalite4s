package com.coxautodata.vegalite4s.providers

import java.io.FileInputStream

/**
  * A VegaLite dependency provider that takes the paths to the
  * Javascript files on the local system and embeds them in the
  * rendered plot using the [[InputStreamProvider]].
  */
object LocalFileProvider {

  def apply(vegaLiteSchemaVersion: String,
            vegaPath: String,
            vegaLitePath: String,
            vegaEmbedPath: String,
            additionalPaths: String*): LibraryProvider =
    new InputStreamProvider(
      (List(vegaPath, vegaLitePath, vegaEmbedPath) ++ additionalPaths)
        .map(new FileInputStream(_)): _*
    )

}
