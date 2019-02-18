package com.coxautodata.vegalite4s.renderers

import java.util.concurrent.{Callable, FutureTask}

import com.coxautodata.vegalite4s.VegaLite
import com.coxautodata.vegalite4s.renderers.RendererUtils.HTMLPageGenerator
import com.sun.javafx.webkit.WebConsoleListener
import com.typesafe.scalalogging.Logger
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.concurrent.Worker
import javafx.embed.swing.JFXPanel
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.web.WebView
import scalafx.stage.Stage

import scala.collection.mutable

/**
  * Class to render a plot by display it in a JavaFX WebView window.
  * Call [[show()]] to render the plot and [[close()]] to close a plot.
  *
  * @param plot VegaLite plot object
  */
case class WindowDisplay(private val plot: VegaLite) {

  /**
    * Boolean indicating whether a plot has finished loading in the WebView
    */
  @volatile var loaded: Boolean = false

  /**
    * List of all Javascript errors from the console
    */
  val errors: mutable.ListBuffer[String] = mutable.ListBuffer.empty

  /**
    * Render a plot as a full-page HTML document.
    * NoOp if a plot is currently being rendered
    */
  def show(): Unit = show(_.htmlPage)

  /**
    * Render a plot by providing a function that generates HTML.
    * NoOp if a plot is currently being rendered
    */
  def show(toHTML: VegaLite => String): Unit =
    maybeStage match {
      case None =>
        init
        val stage = runOnPlatform(createStage(toHTML))
        Platform.runLater(stage.showAndWait())
        maybeStage = Some(stage)
      case Some(_) =>
    }

  /**
    * Close an open plot. NoOp is a plot is not currently being rendered
    */
  def close(): Unit =
    maybeStage match {
      case None =>
      case Some(s) =>
        Platform.runLater(s.close())
        Platform.implicitExit = false
        maybeStage = None
    }

  private val logger: Logger = Logger[WindowDisplay]

  private lazy val init = new JFXPanel()

  private var maybeStage: Option[Stage] = None

  private def runOnPlatform[T](op: => T): T = {
    val task = new FutureTask[T](new Callable[T] {
      override def call(): T = op
    })
    Platform.runLater(task)
    task.get()
  }

  private def createStage(toHTML: VegaLite => String): Stage = {

    // Log errors
    WebConsoleListener.setDefaultListener(new WebConsoleListener {
      def messageAdded(webView: javafx.scene.web.WebView,
                       message: String,
                       lineNumber: Int,
                       sourceId: String): Unit =
        if (message.contains("Error")) {
          errors.append(message)
          logger.error(s"Error in WebView engine: $message")
        } else logger.info(s"Message in WebView engine: $message")
    })

    val webView = new WebView {}
    val webEngine = webView.engine

    val html = toHTML(plot)
    val div = "vegaEmbed\\('#([^']+)',".r
      .findFirstMatchIn(html)
      .map(_.group(1))
      .getOrElse(
        throw new RuntimeException("Could not find Div ID in HTML document")
      )

    // Resize on load and set loaded
    webEngine.getLoadWorker.stateProperty
      .addListener(new ChangeListener[Worker.State]() {
        override def changed(observable: ObservableValue[_ <: Worker.State],
                             oldValue: Worker.State,
                             newValue: Worker.State): Unit =
          if (observable.getValue == Worker.State.SUCCEEDED) {
            loaded = true
            val width = webEngine
              .executeScript(s"document.getElementById('$div').offsetWidth")
              .asInstanceOf[Integer]
            val height =
              webEngine
                .executeScript(
                  s"document.documentElement.getBoundingClientRect().height"
                )
                .asInstanceOf[Integer]
            webView.setPrefSize(width.doubleValue() + 16, height.doubleValue())
            maybeStage.foreach(_.sizeToScene())
          }

      })

    webEngine.loadContent(html)

    new Stage {
      title = plot.toJObject
        .apply("title")
        .flatMap(_.asString)
        .getOrElse("vegalite4s")
      width = 500
      height = 500
      scene = new Scene {
        root = webView
      }
    }
  }

}
