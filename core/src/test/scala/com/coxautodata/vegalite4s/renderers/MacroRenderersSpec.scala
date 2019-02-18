package com.coxautodata.vegalite4s.renderers

import com.coxautodata.vegalite4s.VegaLite
import com.coxautodata.vegalite4s.renderers.ImplicitRenderers.AutoSelectionRenderer
import com.coxautodata.vegalite4s.renderers.RendererUtils.HTMLEmbedGenerator
import org.scalatest.{FunSpec, Matchers}

import scala.language.experimental.macros

class MacroRenderersSpec extends FunSpec with Matchers {

  describe("AutoSelectionRenderer") {
    it("select displayHTML when using available") {

      var result: String = "uncalled"

      def displayHTML(html: String): Unit = {
        result = html
      }

      val plot = VegaLite()
      AutoSelectionRenderer.render(plot)

      result.replaceAll("vegalite4s-[a-z0-9\\-]+", "divName") should be(
        plot.htmlEmbed("divName")
      )

    }

    it("select kernel.display.html when using available") {

      var result: String = "uncalled"

      object kernel {

        object display {
          def html(html: String): Unit = {
            result = html
          }
        }

      }

      val plot = VegaLite()
      AutoSelectionRenderer.render(plot)

      result.replaceAll("vegalite4s-[a-z0-9\\-]+", "divName") should be(
        plot.htmlEmbed("divName")
      )

    }

    it("select publish.html when using available") {

      var result: String = "uncalled"

      object publish {
        def html(html: String): Unit = {
          result = html
        }
      }

      val plot = VegaLite()
      AutoSelectionRenderer.render(plot)

      result.replaceAll("vegalite4s-[a-z0-9\\-]+", "divName") should be(
        plot.htmlEmbed("divName")
      )

    }

    it("select create a zeppelin interpreter if ZeppelinContext available") {

      object org {

        object apache {

          object zeppelin {

            object spark {

              class SparkZeppelinContext

            }

          }

        }

      }
      val z: org.apache.zeppelin.spark.SparkZeppelinContext =
        new org.apache.zeppelin.spark.SparkZeppelinContext

      val plot = VegaLite()

      def zeppelinRenderer: PlotRenderer =
        macro MacroRenderers.sparkZeppelinRenderer

      zeppelinRenderer.render(plot)

    }

    it("select println renderer when nothing else available") {

      val plot = VegaLite()
      AutoSelectionRenderer.render(plot)

    }
  }

}
