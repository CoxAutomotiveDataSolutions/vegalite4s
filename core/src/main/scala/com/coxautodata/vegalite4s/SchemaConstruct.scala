package com.coxautodata.vegalite4s

import com.coxautodata.vegalite4s.providers.SchemaProvider
import com.coxautodata.vegalite4s.renderers.PlotRenderer
import io.circe.{Json, JsonObject}

trait SchemaConstruct[T] extends SpecConstruct[T] {

  def trans: Vector[JsonObject => JsonObject]

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
  lazy val toJObject: JsonObject =
    trans.foldLeft(getProvider.getBaseSchema)((z, t) => t(z))

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
  def getProvider: SchemaProvider

  /**
    * Render the given plot using the renderer provided.
    * Default is to use the [[renderers.ImplicitRenderers.AutoSelectionRenderer]].
    */
  def show(implicit r: PlotRenderer): Unit =
    r.render(this)

}
