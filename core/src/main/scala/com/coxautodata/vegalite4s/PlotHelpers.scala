package com.coxautodata.vegalite4s

import java.sql.{Date, Timestamp}
import java.math.{BigDecimal => JBigDecimal, BigInteger => JBigInteger}

import io.circe.syntax._
import io.circe.{Json, JsonObject, ParsingFailure}

object PlotHelpers {

  implicit class VegaLiteHelpers(plot: VegaLite) {

    /**
      * Add a new layer to the plot. Layer is appended if
      * layers already exist
      *
      * @param spec A function that takes an empty LayerSpec to a full spec
      */
    def withLayer(spec: LayerSpec => LayerSpec): VegaLite =
      plot.withObjectTransformation {
        o =>
          val layer: Vector[Json] = o.apply("layer")
            .getOrElse(Json.fromValues(Iterable.empty))
            .asArray
            .getOrElse(throw new ParsingFailure("Layer field must an array type", null))
            .:+(spec(LayerSpec()).toJObject.asJson)
          o.add("layer", Json.fromValues(layer))
      }

  }

  implicit class SchemaConstructImplicits[T](plot: SchemaConstruct[T]) {

    /**
      * Add a title to the plot
      *
      * @param title Title to use
      */
    def withTitle(title: String): T =
      plot.withField("title", Json.fromString(title))

    /**
      * Set a height value for the current plot
      *
      * @param h Height of the plot
      */
    def withHeight(h: Int): T =
      plot.withField("height", Json.fromInt(h))

    /**
      * Set a width value for the current plot
      *
      * @param w Width of the plot
      */
    def withWidth(w: Int): T =
      plot.withField("width", Json.fromInt(w))

  }

  implicit class SpecConstructImplicits[T](spec: SpecConstruct[T]) {

    /**
      * Add a set of data to the plot.
      * Data is added under the `values` field in the `data` object
      * on the plot.
      *
      * @param values Values to add. Map keys are used as columns names
      */
    def withData(values: => Seq[Map[String, Any]]): T = {

      spec.withField(
        "data",
        Json.fromJsonObject(
          JsonObject("values" -> values.map(_.mapValues(anyEncoder)).asJson)
        )
      )

    }

  }

  def anyEncoder(v: Any): Json = v match {
    case i: Int => Json.fromInt(i)
    case s: Short => Json.fromInt(s)
    case l: Long => Json.fromLong(l)
    case b: Boolean => Json.fromBoolean(b)
    case s: String => Json.fromString(s)
    case bI: BigInt => Json.fromBigInt(bI)
    case bI: JBigInteger => Json.fromBigInt(bI)
    case d: Double => Json.fromDoubleOrString(d)
    case f: Float => Json.fromFloatOrString(f)
    case bD: BigDecimal => Json.fromBigDecimal(bD)
    case bD: JBigDecimal => Json.fromBigDecimal(bD)
    case t: Timestamp => Json.fromString(t.toLocalDateTime.toString)
    case d: Date => Json.fromString(d.toLocalDate.toString)
    case null => Json.Null
    case _ => Json.fromString(v.toString)
  }

}

case class LayerSpec(trans: Vector[JsonObject => JsonObject] = Vector.empty) extends SpecConstruct[LayerSpec] {
  /**
    * Add a generic object transformation to this plot
    *
    * @param t Transformation to add to the current list of transformations
    * @return plot object with transformation applied
    */
  override def withObjectTransformation(t: JsonObject => JsonObject): LayerSpec = LayerSpec(trans :+ t)

  /**
    * Return the current plot definition as a [[JsonObject]]
    */
  lazy val toJObject: JsonObject =
    trans.foldLeft(JsonObject())((z, t) => t(z))
}