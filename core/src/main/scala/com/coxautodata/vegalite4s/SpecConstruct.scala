package com.coxautodata.vegalite4s

import io.circe.{Json, JsonObject, ParsingFailure}
import io.circe.parser.parse

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
