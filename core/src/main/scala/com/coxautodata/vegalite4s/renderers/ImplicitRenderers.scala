package com.coxautodata.vegalite4s.renderers

import com.coxautodata.vegalite4s.{SchemaConstruct, VegaLite}

import scala.language.experimental.macros

object ImplicitRenderers {

  /**
    * Renderer that prints a full page HTML document to stdout
    */
  implicit object HTMLPagePrintlnRenderer
      extends HTMLRenderer(println(_), fullHTMLPage = true)

  /**
    * Renderer that visualises the plot object in a Javafx WebView window
    */
  implicit object WindowRenderer extends PlotRenderer {
    override def render(plot: SchemaConstruct[_]): Unit = WindowDisplay(plot).show()
  }

  /**
    * Auto-selection renderer defined in [[MacroRenderers]]
    *
    * @return
    */
  implicit def AutoSelectionRenderer: PlotRenderer =
    macro MacroRenderers.autoSelection

}
