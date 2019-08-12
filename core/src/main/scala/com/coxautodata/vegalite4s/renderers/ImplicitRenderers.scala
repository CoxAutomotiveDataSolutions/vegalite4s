package com.coxautodata.vegalite4s.renderers

import scala.language.experimental.macros

object ImplicitRenderers {

  /**
    * Renderer that prints a full page HTML document to stdout
    */
  implicit object HTMLPagePrintlnRenderer
    extends HTMLRenderer(println(_), fullHTMLPage = true)

  /**
    * Auto-selection renderer defined in [[MacroRenderers]]
    *
    * @return
    */
  implicit def AutoSelectionRenderer: PlotRenderer =
  macro MacroRenderers.autoSelection

}
