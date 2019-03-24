package com.coxautodata.vegalite4s

import io.circe.{Json, JsonObject}

package object providers {

  /**
    * A Vega dependency provider that references the latest available VegaLite library
    * available on the Jsdelivr CDN
    */
  object LatestCDNVersion extends JsdelivrProvider("5", "3", "4")

  /**
    * Generic trait used to represent a class providing a mechanism
    * to load Javascript dependencies for VegaLite plots
    */
  trait VegaLiteProvider {

    /**
      * The VegaLite version referenced in the plot schema
      */
    def vegaLiteSchemaVersion: String

    /**
      * Base schema from which all plots are created
      */
    final def getBaseSchema: JsonObject =
      JsonObject("$schema" -> Json.fromString(
        s"https://vega.github.io/schema/vega-lite/v$vegaLiteSchemaVersion.json"))

    /**
      * List of Javascript libraries to include.
      * These can be URLs, or any other string that can go in a
      * `src` field (see [[InputStreamProvider]]).
      */
    def getJavascriptLibraryURLs: Seq[String]

  }

}
