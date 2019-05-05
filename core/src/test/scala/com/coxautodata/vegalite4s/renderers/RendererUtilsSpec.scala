package com.coxautodata.vegalite4s.renderers

import com.coxautodata.vegalite4s.VegaLite
import com.coxautodata.vegalite4s.providers.{JsdelivrProvider, VegaLiteProvider}
import org.scalatest.{FunSpec, Matchers}

class RendererUtilsSpec extends FunSpec with Matchers {

  describe("JavascriptGenerator") {

    it("create a simple VegaEmbed script") {
      import RendererUtils.JavascriptGenerator

      VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2"))
        .asVegaEmbedScriptDefinition("test", includeDynamicLoader = false)
        .trim should be(
        """
          |var vlSpec = {"$schema":"https://vega.github.io/schema/vega-lite/v2.json"};
          |vegaEmbed('#test', vlSpec);
        """.stripMargin.trim
      )

    }

    it("create a VegaEmbed script with a dynamic loader") {
      import RendererUtils.JavascriptGenerator

      VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2"))
        .asVegaEmbedScriptDefinition("test", includeDynamicLoader = true)
        .trim should be(
        """
          |var vlSpec = {"$schema":"https://vega.github.io/schema/vega-lite/v2.json"};
          |
          |function isScriptAlreadyIncluded(src) {
          |    var scripts = document.getElementsByTagName("script");
          |    for (var i = 0; i < scripts.length; i++)
          |        if (scripts[i].getAttribute('src') == src) return true;
          |    return false;
          |}
          |
          |function loadScriptsThenRender(urls) {
          |    if (urls.length == 0) {
          |        vegaEmbed('#test', vlSpec);
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
          |var files = ["https://cdn.jsdelivr.net/npm/vega@3", "https://cdn.jsdelivr.net/npm/vega-lite@2", "https://cdn.jsdelivr.net/npm/vega-embed@3"]
          |loadScriptsThenRender(files);
          |""".stripMargin.trim
      )

    }

  }

  describe("HTMLPageGenerator") {

    it("create a full page HTML document with a given div") {
      import RendererUtils.HTMLPageGenerator

      VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2"))
        .htmlPage("test")
        .trim should be(
        """
          |<!DOCTYPE html>
          |<html>
          |  <head>
          |    <meta charset="utf-8">
          |    <script src="https://cdn.jsdelivr.net/npm/vega@3"></script>
          |<script src="https://cdn.jsdelivr.net/npm/vega-lite@2"></script>
          |<script src="https://cdn.jsdelivr.net/npm/vega-embed@3"></script>
          |  </head>
          |  <body>
          |    <div id="test"></div>
          |
          |    <script type="text/javascript">
          |
          |var vlSpec = {"$schema":"https://vega.github.io/schema/vega-lite/v2.json"};
          |vegaEmbed('#test', vlSpec);
          |    </script>
          |  </body>
          |</html>
        """.stripMargin.trim
      )

    }

    it(
      "rendering the same plot twice should produce the same html when using the same div"
    ) {
      import RendererUtils.HTMLPageGenerator

      (VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2")).htmlPage("test")
        should be(VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2")).htmlPage("test")))

    }

    it(
      "rendering the same plot twice should produce different html when using a random div"
    ) {
      import RendererUtils.HTMLPageGenerator

      (
        VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2")).htmlPage
          should not be
          VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2")).htmlPage
      )

    }

  }

  describe("HTMLEmbedGenerator") {

    it("create a section of a HTML document with a given div") {
      import RendererUtils.HTMLEmbedGenerator

      VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2"))
        .htmlEmbed("test")
        .trim should be(
        """
          |<div id="test"></div>
          |
          |<script type="text/javascript">
          |
          |var vlSpec = {"$schema":"https://vega.github.io/schema/vega-lite/v2.json"};
          |
          |function isScriptAlreadyIncluded(src) {
          |    var scripts = document.getElementsByTagName("script");
          |    for (var i = 0; i < scripts.length; i++)
          |        if (scripts[i].getAttribute('src') == src) return true;
          |    return false;
          |}
          |
          |function loadScriptsThenRender(urls) {
          |    if (urls.length == 0) {
          |        vegaEmbed('#test', vlSpec);
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
          |var files = ["https://cdn.jsdelivr.net/npm/vega@3", "https://cdn.jsdelivr.net/npm/vega-lite@2", "https://cdn.jsdelivr.net/npm/vega-embed@3"]
          |loadScriptsThenRender(files);
          |
          |</script>
        """.stripMargin.trim
      )

    }

    it(
      "rendering the same plot twice should produce the same html when using the same div"
    ) {
      import RendererUtils.HTMLEmbedGenerator

      (VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2")).htmlEmbed("test")
        should be(VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2")).htmlEmbed("test")))

    }

    it(
      "rendering the same plot twice should produce different html when using a random div"
    ) {
      import RendererUtils.HTMLEmbedGenerator

      (
        VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2")).htmlEmbed
          should not be
          VegaLite(VegaLiteProvider(JsdelivrProvider("3", "2", "3"), "2")).htmlEmbed
      )

    }

  }

}
