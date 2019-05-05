package com.coxautodata.vegalite4s

import io.circe.{Json, JsonObject}

package object providers {

  /**
    * A Vega dependency provider that references the latest available VegaLite library
    * available on the Jsdelivr CDN
    */
  object VegaLiteLatestCDNVersion extends VegaLiteProvider(JsdelivrProvider("5", "3", "4"), "3")

  /**
    * A Vega dependency provider that references the latest available Vega library
    * available on the Jsdelivr CDN
    */
  object VegaLatestCDNVersion extends VegaProvider(JsdelivrProvider("5", "3", "4"), "5")

  /**
    * A class providing a mechanism
    * to load Javascript dependencies for VegaLite plots
    */
  case class VegaLiteProvider(libraryProvider: LibraryProvider, vegaLiteVersion: String) extends SchemaProvider {

    final def getBaseSchema: JsonObject =
      JsonObject("$schema" -> Json.fromString(
        s"https://vega.github.io/schema/vega-lite/v$vegaLiteVersion.json"))

    override def getJavascriptLibraryURLs: Seq[String] = libraryProvider.getJavascriptLibraryURLs
  }

  case class VegaProvider(libraryProvider: LibraryProvider, vegaVersion: String) extends SchemaProvider {

    /**
      * Base schema from which all plots are created
      */
    final def getBaseSchema: JsonObject =
      JsonObject("$schema" -> Json.fromString(
        s"https://vega.github.io/schema/vega/v$vegaVersion.json"))

    /**
      * List of Javascript libraries to include.
      * These can be URLs, or any other string that can go in a
      * `src` field (see [[InputStreamProvider]]).
      */
    override def getJavascriptLibraryURLs: Seq[String] = libraryProvider.getJavascriptLibraryURLs
  }

  trait SchemaProvider extends LibraryProvider {

    /**
      * Base schema from which all plots are created
      */
    def getBaseSchema: JsonObject

  }

  trait LibraryProvider {

    /**
      * List of Javascript libraries to include.
      * These can be URLs, or any other string that can go in a
      * `src` field (see [[InputStreamProvider]]).
      */
    def getJavascriptLibraryURLs: Seq[String]

  }

}
