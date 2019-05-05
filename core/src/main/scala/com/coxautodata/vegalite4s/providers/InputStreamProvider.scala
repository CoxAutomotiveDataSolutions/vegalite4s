package com.coxautodata.vegalite4s.providers

import java.io.InputStream
import java.util.Base64

import scala.io.Source

/**
  * A provider class that provides the Vega libraries for rendering plots by
  * reading the Javascript sources as InputStreams and creating a Base64 src string
  * so they can be included in a HTML document directly without referencing an external
  * Javascript library.
  * Using this class or one of the providers using this class (i.e. [[ClasspathJarResourceProvider]]
  * or [[LocalFileProvider]]) allows VegaLite plots to be rendered in an environment
  * without direct access to CDNs.
  *
  * @param libraryStreams A list of InputStreams to include as Javascript libraries
  */
class InputStreamProvider(libraryStreams: InputStream*)
    extends LibraryProvider {

  def streamToJavascriptEmbed(in: InputStream): String = {
    val script = Source.fromInputStream(in, "UTF-8").mkString.getBytes("UTF-8")

    s"data:text/javascript;base64,${Base64.getEncoder.encodeToString(script)}"
  }

  override val getJavascriptLibraryURLs: Seq[String] =
    libraryStreams.map(streamToJavascriptEmbed)
}
