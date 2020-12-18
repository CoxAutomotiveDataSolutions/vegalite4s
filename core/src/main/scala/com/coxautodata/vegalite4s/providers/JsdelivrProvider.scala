package com.coxautodata.vegalite4s.providers

/**
  * VegaLite Javascript dependency provider that references the Vega
  * dependency by providing a URL to the Jsdelivr CDN.
  *
  * @param vegaVersion      Version of Vega to reference
  * @param vegaLiteVersion  Version of Vega-lite to reference
  * @param vegaEmbedVersion Version of Vega-embed to reference
  */
case class JsdelivrProvider(vegaVersion: String,
                            vegaLiteVersion: String,
                            vegaEmbedVersion: String,
                            additionalLibraries: Seq[(String, String)] = Seq.empty
                           ) extends LibraryProvider {

  override def getJavascriptLibraryURLs: Seq[String] = {
    Seq(
      "vega" -> vegaVersion,
      "vega-lite" -> vegaLiteVersion,
      "vega-embed" -> vegaEmbedVersion
    ) ++ additionalLibraries
  }.map { case (k, v) => s"https://cdn.jsdelivr.net/npm/$k@$v" }


}
