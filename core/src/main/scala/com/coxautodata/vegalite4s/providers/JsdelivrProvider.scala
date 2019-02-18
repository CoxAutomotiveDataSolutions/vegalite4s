package com.coxautodata.vegalite4s.providers

/**
  * VegaLite Javascript dependency provider that references the Vega
  * dependency by providing a URL to the Jsdelivr CDN.
  *
  * @param vegaVersion Version of Vega to reference
  * @param vegaLiteVersion Version of Vega-lite to reference
  * @param vegaEmbedVersion Version of Vega-embed to reference
  */
case class JsdelivrProvider(vegaVersion: String,
                            vegaLiteVersion: String,
                            vegaEmbedVersion: String)
    extends VegaLiteProvider {

  override def getJavascriptLibraryURLs: Seq[String] =
    Seq(
      s"https://cdn.jsdelivr.net/npm/vega@$vegaVersion",
      s"https://cdn.jsdelivr.net/npm/vega-lite@$vegaLiteVersion",
      s"https://cdn.jsdelivr.net/npm/vega-embed@$vegaEmbedVersion"
    )

  override def vegaLiteSchemaVersion: String = vegaLiteVersion
}
