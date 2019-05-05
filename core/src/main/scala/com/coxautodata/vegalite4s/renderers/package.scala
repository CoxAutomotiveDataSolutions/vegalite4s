package com.coxautodata.vegalite4s

package object renderers {

  /**
    * Generic renderer trait used to render/show a plot
    */
  trait PlotRenderer {

    /**
      * The function used by the render object to visualise/render the plot object
      * @param plot VegaLite plot object
      */
    def render(plot: SchemaConstruct[_]): Unit
  }

}
