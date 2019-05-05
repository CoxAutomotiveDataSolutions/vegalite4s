package com.coxautodata.vegalite4s

import com.coxautodata.vegalite4s.providers.{LatestCDNVersion, VegaLiteProvider}
import com.coxautodata.vegalite4s.renderers.{PlotRenderer, WindowDisplay}
import io.circe.{Json, JsonObject, ParsingFailure}
import io.circe.parser._

/**
  * VegaLite plot class. All plots are constructed from an object of this time.
  * It is more convenient to use the provided companion object
  * to create new VegaLite plots `val plot = VegaLite()`.
  *
  * @param prov  A [[com.coxautodata.vegalite4s.providers.VegaLiteProvider]] object that provides the VegaLite dependency
  * @param trans A series of transformation to perform on the plot that generates the final
  *              plot object
  */
class VegaLite(prov: VegaLiteProvider,
               trans: Vector[JsonObject => JsonObject]) extends SpecConstruct[VegaLite] {

  /**
    * Add a generic object transformation to this plot
    *
    * @param t Transformation to add to the current list of transformations
    * @return plot object with transformation applied
    */
  def withObjectTransformation(t: JsonObject => JsonObject): VegaLite =
    new VegaLite(prov, trans :+ t)

  /**
    * Return the current plot definition as a [[JsonObject]]
    */
  lazy val toJObject: JsonObject =
    trans.foldLeft(prov.getBaseSchema)((z, t) => t(z))

  /**
    * Return the current plot definition as a JSON string
    */
  def toJson(toString: Json => String): String = toString(Json.fromJsonObject(toJObject))

  /**
    * Return the current plot definition as a JSON string
    */
  def toJson: String = toJson(_.noSpaces)

  /**
    * Get the provider used by this plot
    */
  def getProvider: VegaLiteProvider = prov

  /**
    * Render the given plot using the renderer provided.
    * Default is to use the [[renderers.ImplicitRenderers.AutoSelectionRenderer]].
    */
  def show(implicit r: PlotRenderer): Unit =
    r.render(this)

  /**
    * The [[WindowDisplay]] renderer associated with this plot.
    * Not initialised unless called.
    */
  lazy val window = WindowDisplay(this)

}

/**
  * Companion object to construct an empty [[VegaLite]] plot object
  */
object VegaLite {

  /**
    * Create an empty plot object
    *
    * @param provider Provides the VegaLite dependency. Default is to use
    *                 the latest version from the JSdelivr CDN
    * @return Empty plot object
    */
  def apply(provider: VegaLiteProvider = LatestCDNVersion): VegaLite =
    new VegaLite(provider, Vector.empty)
}

trait SpecConstruct[T] {

  /**
    * Add a json object to the current plot definition as a raw JSON string.
    * String must be a valid JSON object and existing fields will be overwritten
    * by any identical fields in the object provided
    *
    * @param json Json object as a string
    * @return plot object with transformation applied
    */
  def withObject(json: String): T =
    parse(json).right.map(_.asObject) match {
      case Right(Some(o)) => withObject(o)
      case Right(None) =>
        throw new ParsingFailure(
          s"JSON input was not a valid JSON object [$json]",
          null)
      case Left(e) => throw e
    }

  /**
    * Add a json object to the current plot definition. Existing fields
    * will be overwritten by any identical fields in the object provided
    *
    * @param o Json object
    * @return plot object with transformation applied
    */
  def withObject(o: JsonObject): T =
    withObjectTransformation(o.toIterable.foldLeft(_) {
      case (z, (k, v)) => z.add(k, v)
    })

  /**
    * Add a field to the current plot definition as a raw JSON string.
    * String must be a valid JSON value and existing fields will be overwritten
    * if an identical field is given.
    *
    * @param field Field name of the value to add
    * @param value Value as a valid json value
    * @return plot object with transformation applied
    */
  def withField(field: String, value: String): T = parse(value) match {
    case Right(j) => withObjectTransformation(_.add(field, j))
    case Left(e) => throw e
  }

  /**
    * Add a field to the current plot definition. Existing fields will
    * be overwritten if an identical field is given.
    *
    * @param field Field name of the value to add
    * @param value Value
    * @return plot object with transformation applied
    */
  def withField(field: String, value: => Json): T =
    withObjectTransformation(_.add(field, value))

  /**
    * Add a generic object transformation to this plot
    *
    * @param t Transformation to add to the current list of transformations
    * @return plot object with transformation applied
    */
  def withObjectTransformation(t: JsonObject => JsonObject): T

  /**
    * Return the current plot definition as a [[JsonObject]]
    */
  def toJObject: JsonObject

}