package com.coxautodata.vegalite4s.renderers

import java.util.UUID

import com.coxautodata.vegalite4s.{SchemaConstruct, VegaLite}

/**
  * Various utility functions for turning a [[VegaLite]] object
  * into something useful
  */
object RendererUtils {

  private def getUniqueDivName = s"vegalite4s-${UUID.randomUUID.toString}"

  /**
    * Implicit class for generating Javascript functions to render a
    * plot
    *
    * @param plot plot object
    */
  implicit class JavascriptGenerator(plot: SchemaConstruct[_]) {

    /**
      * Generate a Javascript script for rendering a VegaLite plot using VegaEmbed.
      * vegaEmbed will insert the Vega object into the provided Div ID.
      * The `includeDynamicLoader` flag is used to either generate a script to dynamically
      * load the Vega libraries if true, or assume they have already been loaded and only
      * generate the call to VegaEmbed if false.
      *
      * @param divTag               Which Div ID to target
      * @param includeDynamicLoader Whether to include a dynamic loader for Vega dependencies
      * @return Javascript code as string
      */
    def asVegaEmbedScriptDefinition(divTag: String,
                                    includeDynamicLoader: Boolean): String =
      s"""
         |var $specVarName = ${plot.toJson};
         |""".stripMargin + (if (includeDynamicLoader) vegaDynamicLoader(divTag)
                             else vegaEmbedCall(divTag))

    private val specVarName = "vlSpec"

    private def vegaEmbedCall(divTag: String): String =
      s"vegaEmbed('#$divTag', $specVarName);"

    private def vegaDynamicLoader(divTag: String): String =
      s"""
         |function isScriptAlreadyIncluded(src) {
         |    var scripts = document.getElementsByTagName("script");
         |    for (var i = 0; i < scripts.length; i++)
         |        if (scripts[i].getAttribute('src') == src) return true;
         |    return false;
         |}
         |
         |function loadScriptsThenRender(urls) {
         |    if (urls.length == 0) {
         |        ${vegaEmbedCall(divTag)}
         |    } else {
         |        var url = urls[0];
         |        urls.shift();
         |        var nextLoad = function() {
         |            loadScriptsThenRender(urls);
         |        }
         |
         |        if (isScriptAlreadyIncluded(url) == true) {
         |            nextLoad();
         |        } else {
         |            var scriptTag = document.createElement('script');
         |            scriptTag.src = url;
         |            scriptTag.onload = nextLoad;
         |            scriptTag.onreadystatechange = nextLoad;
         |            document.body.appendChild(scriptTag);
         |        }
         |
         |    }
         |};
         |var files = ${plot.getProvider.getJavascriptLibraryURLs
           .map('"' + _ + '"')
           .mkString("[", ", ", "]")}
         |loadScriptsThenRender(files);
         |""".stripMargin

  }

  /**
    * Implicit class for turning a VegaLite plot into a full HTML page.
    * This page will include `<html>`, `<head>` and `<body>` tags and
    * Javascript resources will be loaded by `<script>` tags in the head.
    *
    * If you are embedding HTML into an existing page (e.g. in a Notebook)
    * you should use the [[HTMLEmbedGenerator]] class.
    *
    * @param vegaLite VegaLite plot object
    */
  implicit class HTMLPageGenerator(vegaLite: SchemaConstruct[_]) {

    private def scriptImports: String =
      vegaLite.getProvider.getJavascriptLibraryURLs
        .map(l => s"""<script src="$l"></script>""")
        .mkString("\n")

    /**
      * Create a full HTML page with a random unique div id
      *
      * @return Plot as a HTML string
      */
    def htmlPage: String = htmlPage(getUniqueDivName)

    /**
      * Create a full HTML page with a specific div id
      *
      * @param divName ID of the div to generate the plot in
      * @return Plot as a HTML string
      */
    def htmlPage(divName: String): String =
      (s"""
          |<!DOCTYPE html>
          |<html>
          |  <head>
          |    <meta charset="utf-8">
          |    $scriptImports
          |  </head>
          |  <body>
          |    <div id="$divName"></div>
          |
          |    <script type="text/javascript">
          |""".stripMargin
        +
          vegaLite.asVegaEmbedScriptDefinition(
            divName,
            includeDynamicLoader = false
          )
        +
          """
          |    </script>
          |  </body>
          |</html>
        """.stripMargin)
  }

  /**
    * Implicit class for turning a VegaLite plot into a section of a HTML document.
    * The output will only include a `<div>` block and a `<script>` block.
    * Javascript resources will be loaded dynamically in the `<script>` block.
    * Useful for embedding a plot into an existing page (e.g. in a Notebook).
    *
    * To generate a full HTML document use the [[HTMLPageGenerator]] class.
    *
    * @param vegaLite VegaLite plot object
    */
  implicit class HTMLEmbedGenerator(vegaLite: SchemaConstruct[_]) {

    /**
      * Generate a subset of a HTML page including
      * a `<div>` and `<script>` block. The Div ID
      * will be random unique.
      *
      * @return Plot as a HTML string
      */
    def htmlEmbed: String = htmlEmbed(getUniqueDivName)

    /**
      * Generate a subset of a HTML page including
      * a `<div>` and `<script>` block. The Div ID
      * will be random unique.
      *
      * @param divName ID of the Div
      * @return Plot as a HTML string
      */
    def htmlEmbed(divName: String): String =
      (s"""
          |<div id="$divName"></div>
          |
          |<script type="text/javascript">
          |""".stripMargin
        +
          vegaLite.asVegaEmbedScriptDefinition(
            divName,
            includeDynamicLoader = true
          )
        +
          """
          |</script>
        """.stripMargin)

  }

}
