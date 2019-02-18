package com.coxautodata.vegalite4s.renderers

import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import scala.util.{Success, Try}

/**
  * A renderer class used to create plot renderers at compile-time allowing
  * an appropriate renderer to be chosen given a users current environment.
  * This class is most useful in Notebook/Shell environments where the
  * correct rendering function is chosen depending on certain functions available
  * in the users session, i.e. if running in a Databricks notebook then the
  * `displayHTML` function is used to render HTML if it is available.
  *
  * This class is used as a macro, for example:
  * ```
  * import scala.language.experimental.macros
  * val renderer = macro MacroRenderers.autoSelection
  * ```
  *
  * @param c Scala macro context
  */
class MacroRenderers(val c: whitebox.Context) {

  import c.universe.{Try => _, _}

  /**
    * Auto-selection renderer, that currently will attempt to detect the following environments
    * in this order (see specific render for details):
    * - Databricks Notebook as [[databricksRenderer]]
    * - Spark environment as [[sparkInterpreterRenderer]]
    * - Fallback as [[printlnRenderer]]
    *
    * @return Renderer chosen at compile-time
    */
  def autoSelection: Tree =
    Seq(
      databricksDisplayFunction,
      toreeDisplayFunction,
      almondDisplayFunction,
      sparkZeppelinFunction,
      sparkInterpreterDisplayFunction,
      printlnDisplayFunction
    ).map(s => Try((getRenderer _).tupled(s)))
      .collectFirst { case Success(t) => t }
      .getOrElse(
        c.abort(
          c.enclosingPosition,
          "No renderer could be selected automatically"
        )
      )

  /**
    * Renderer used to print an embedded HTML section using the `displayHTML` function that should be
    * available in the Notebook session.
    * This will fail to compile if the `displayHTML` function is not available.
    *
    * @return Renderer for Databricks notebooks.
    */
  def databricksRenderer: Tree =
    (getRenderer _).tupled(databricksDisplayFunction)

  /**
    * Renderer used to print an embedded HTML section using the `kernel.display.html` function that should be
    * available in the Notebook session.
    * This will fail to compile if the `kernel.display.html` function is not available.
    *
    * @return Renderer for Toree interpreter sessions.
    */
  def toreeRenderer: Tree =
    (getRenderer _).tupled(toreeDisplayFunction)

  /**
    * Renderer used to print an embedded HTML section using the `publish.html` function that should be
    * available in the Notebook session.
    * This will fail to compile if the `publish.html` function is not available.
    *
    * @return Renderer for Almond interpreter sessions.
    */
  def almondRenderer: Tree =
    (getRenderer _).tupled(almondDisplayFunction)

  /**
    * Renderer used to print an embedded HTML section using `println(s"%html\n$html")`,
    * but first detects a Zeppelin session by looking for the `z: SparkZeppelinContext` object.
    * This will fail to compile if the `z: SparkZeppelinContext` object is not available.
    *
    * @return Renderer for Zeppelin Spark interpreter sessions.
    */
  def sparkZeppelinRenderer: Tree =
    (getRenderer _).tupled(sparkZeppelinFunction)

  /**
    * Renderer used to print an embedded HTML section if a `spark: SparkSession` object is in scope.
    * Currently, this renderer will attempt to detect the Spark environment in the following order:
    * - Print an embedded HTML block preceded by `%html` if a Livy interpreter environment was found
    * - Print an embedded HTML block as a fallback
    *
    * @return Renderer for a Spark environment
    */
  def sparkInterpreterRenderer: Tree =
    (getRenderer _).tupled(sparkInterpreterDisplayFunction)

  /**
    * Renderer used to print a full HTML page using `println`
    *
    * @return Renderer using `println` for printing
    */
  def printlnRenderer: Tree = (getRenderer _).tupled(printlnDisplayFunction)

  private def getRenderer(displayFunction: String,
                          fullHTMLPage: Boolean): Tree =
    c.typecheck(
      q"com.coxautodata.vegalite4s.renderers.HTMLRenderer(${c
        .typecheck(c.parse(displayFunction))}, fullHTMLPage = ${c.parse(fullHTMLPage.toString)})"
    )

  private val databricksDisplayFunction: (String, Boolean) =
    ("(html: String) => { displayHTML(html) }", false)

  private val toreeDisplayFunction: (String, Boolean) =
    ("(html: String) => { kernel.display.html(html) }", false)

  private val almondDisplayFunction: (String, Boolean) =
    ("(html: String) => { publish.html(html) }", false)

  private val sparkZeppelinFunction: (String, Boolean) =
    ("""(html: String) => {
        |  val _: org.apache.zeppelin.spark.SparkZeppelinContext = z
        |  println(s"%html\n$html")
        |}""".stripMargin,
     false)

  private val sparkInterpreterDisplayFunction: (String, Boolean) =
    ("""(html: String) => {
        |  val sparkSession: org.apache.spark.sql.SparkSession = spark
        |  if (sparkSession.conf.getAll.get("spark.livy.spark_major_version").isDefined) println(s"%html\n$html")
        |  else println(html)
        |}""".stripMargin,
     false)

  private val printlnDisplayFunction: (String, Boolean) =
    ("(html: String) => { println(html) }", true)

}
