package com.coxautodata.vegalite4s.renderers

import com.coxautodata.vegalite4s.VegaLite
import com.coxautodata.vegalite4s.providers.{
  ClasspathJarResourceProvider,
  InputStreamProvider,
  JsdelivrProvider
}
import com.coxautodata.vegalite4s.renderers.RendererUtils.HTMLEmbedGenerator
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class WindowDisplaySpec extends FunSpec with Matchers {

  /**
    * Helper function that returns a future that completes when a plot has loaded
    */
  def waitToLoad(window: WindowDisplay): Future[Unit] =
    Future {
      Stream
        .continually[Boolean] {
          Thread.sleep(100); window.loaded
        }
        .contains(true)
    }

  describe("WindowDisplay full page") {

    it("render a vega-lite plot with no errors") {

      val window = VegaLite(JsdelivrProvider("3", "2", "3"))
        .withObject(
          """
            |{
            |  "$schema": "https://vega.github.io/schema/vega-lite/v3.json",
            |  "description": "A simple bar chart with embedded data.",
            |  "title": "Example plot",
            |  "data": {
            |    "values": [
            |      {"a": "A","b": 28}, {"a": "B","b": 55}, {"a": "C","b": 43},
            |      {"a": "D","b": 91}, {"a": "E","b": 81}, {"a": "F","b": 53},
            |      {"a": "G","b": 19}, {"a": "H","b": 87}, {"a": "I","b": 52}
            |    ]
            |  },
            |  "mark": "bar",
            |  "encoding": {
            |    "x": {"field": "a", "type": "ordinal"},
            |    "y": {"field": "b", "type": "quantitative"}
            |  }
            |}
            |""".stripMargin
        )
        .window

      window.show()

      Await.result(waitToLoad(window), 5 seconds)

      window.errors.toList should be(List.empty)
      window.loaded should be(true)

      window.close()

    }

    it("render a vega-lite plot using an InputStream provider with no errors") {

      val window = VegaLite(ClasspathJarResourceProvider("3"))
        .withObject(
          """
            |{
            |  "$schema": "https://vega.github.io/schema/vega-lite/v3.json",
            |  "description": "A simple bar chart with embedded data.",
            |  "title": "Example plot",
            |  "data": {
            |    "values": [
            |      {"a": "A","b": 28}, {"a": "B","b": 55}, {"a": "C","b": 43},
            |      {"a": "D","b": 91}, {"a": "E","b": 81}, {"a": "F","b": 53},
            |      {"a": "G","b": 19}, {"a": "H","b": 87}, {"a": "I","b": 52}
            |    ]
            |  },
            |  "mark": "bar",
            |  "encoding": {
            |    "x": {"field": "a", "type": "ordinal"},
            |    "y": {"field": "b", "type": "quantitative"}
            |  }
            |}
            |""".stripMargin
        )
        .window

      window.show()

      Await.result(waitToLoad(window), 5 seconds)

      window.errors.toList should be(List.empty)
      window.loaded should be(true)

      window.close()

    }
  }

  describe("WindowDisplay embedded") {

    def toHTML(plot: VegaLite): String = {
      ("""
         |<!DOCTYPE html>
         |<html>
         |  <head>
         |    <meta charset="utf-8">
         |  </head>
         |<body>
       """.stripMargin
        +
          plot.htmlEmbed
        +
          """
          |  </body>
          |</html>
        """.stripMargin)
    }

    it("render a vega-lite plot with no errors using an embedded document") {

      val window = VegaLite(JsdelivrProvider("3", "2", "3"))
        .withObject(
          """
            |{
            |  "$schema": "https://vega.github.io/schema/vega-lite/v3.json",
            |  "description": "A simple bar chart with embedded data.",
            |  "title": "Example plot",
            |  "data": {
            |    "values": [
            |      {"a": "A","b": 28}, {"a": "B","b": 55}, {"a": "C","b": 43},
            |      {"a": "D","b": 91}, {"a": "E","b": 81}, {"a": "F","b": 53},
            |      {"a": "G","b": 19}, {"a": "H","b": 87}, {"a": "I","b": 52}
            |    ]
            |  },
            |  "mark": "bar",
            |  "encoding": {
            |    "x": {"field": "a", "type": "ordinal"},
            |    "y": {"field": "b", "type": "quantitative"}
            |  }
            |}
            |""".stripMargin
        )
        .window

      window.show(toHTML)

      Await.result(waitToLoad(window), 5 seconds)

      window.errors.toList should be(List.empty)
      window.loaded should be(true)

      window.close()

    }

    it(
      "render a vega-lite plot using an InputStream provider with no errors using an embedded document"
    ) {

      val window = VegaLite(ClasspathJarResourceProvider("3"))
        .withObject(
          """
            |{
            |  "$schema": "https://vega.github.io/schema/vega-lite/v3.json",
            |  "description": "A simple bar chart with embedded data.",
            |  "title": "Example plot",
            |  "data": {
            |    "values": [
            |      {"a": "A","b": 28}, {"a": "B","b": 55}, {"a": "C","b": 43},
            |      {"a": "D","b": 91}, {"a": "E","b": 81}, {"a": "F","b": 53},
            |      {"a": "G","b": 19}, {"a": "H","b": 87}, {"a": "I","b": 52}
            |    ]
            |  },
            |  "mark": "bar",
            |  "encoding": {
            |    "x": {"field": "a", "type": "ordinal"},
            |    "y": {"field": "b", "type": "quantitative"}
            |  }
            |}
            |""".stripMargin
        )
        .window

      window.show(toHTML)

      Await.result(waitToLoad(window), 5 seconds)

      window.errors.toList should be(List.empty)
      window.loaded should be(true)

      window.close()

    }
  }

  describe("WindowDisplay errors") {

    it("render a vega-lite plot with errors") {

      val window = VegaLite(new InputStreamProvider("3"))
        .withObject(
          """
            |{
            |  "$schema": "https://vega.github.io/schema/vega-lite/v3.json",
            |  "description": "A simple bar chart with embedded data.",
            |  "title": "Example plot",
            |  "data": {
            |    "values": [
            |      {"a": "A","b": 28}, {"a": "B","b": 55}, {"a": "C","b": 43},
            |      {"a": "D","b": 91}, {"a": "E","b": 81}, {"a": "F","b": 53},
            |      {"a": "G","b": 19}, {"a": "H","b": 87}, {"a": "I","b": 52}
            |    ]
            |  },
            |  "mark": "bar",
            |  "encoding": {
            |    "x": {"field": "a", "type": "ordinal"},
            |    "y": {"field": "b", "type": "quantitative"}
            |  }
            |}
            |""".stripMargin
        )
        .window

      window.show()

      Await.result(waitToLoad(window), 10 seconds)

      window.errors.toList should be(
        List("ReferenceError: Can't find variable: vegaEmbed")
      )
      window.loaded should be(true)

      window.close()

    }

  }

}
