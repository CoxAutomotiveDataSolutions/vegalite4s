package com.coxautodata.vegalite4s.renderers

import com.coxautodata.vegalite4s.{SchemaConstruct, VegaLite}
import com.coxautodata.vegalite4s.renderers.RendererUtils._

/**
  * Generic HTML renderer used to create a specific renderer given HTML output.
  *
  * @param render       Function to render the generated HTML output
  * @param fullHTMLPage Whether to produce a full-page HTML output (including `<html>`, `<head>` and `<body>`
  *                     tags) with Javascript resources loaded by `<script>` tags in the head, or to generate a
  *                     HTML section to be embedded in another page that loads Javascript resources dynamically.
  *                     See [[RendererUtils.HTMLPageGenerator]] and [[RendererUtils.HTMLEmbedGenerator]] for details.
  */
case class HTMLRenderer(render: String => Unit, fullHTMLPage: Boolean)
    extends PlotRenderer {

  override def render(plot: SchemaConstruct[_]): Unit =
    if (fullHTMLPage) render(plot.htmlPage)
    else render(plot.htmlEmbed)

}
