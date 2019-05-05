package com.coxautodata.vegalite4s

import com.coxautodata.vegalite4s.providers.{SchemaProvider, VegaLatestCDNVersion, VegaProvider}
import io.circe.JsonObject

/**
  * Vega plot class. All plots are constructed from an object of this time.
  * It is more convenient to use the provided companion object
  * to create new Vega plots `val plot = Vega()`.
  Vega
  * @param prov  A [[com.coxautodata.vegalite4s.providers.VegaProvider]] object that provides the Vega dependency
  * @param trans A series of transformation to perform on the plot that generates the final
  *              plot object
  */
class Vega(prov: VegaProvider,
               override val trans: Vector[JsonObject => JsonObject]) extends SchemaConstruct[Vega] {

  override def withObjectTransformation(t: JsonObject => JsonObject): Vega = new Vega(prov, trans :+ t)

  override def getProvider: SchemaProvider = prov
}

/**
  * Companion object to construct an empty [[Vega]] plot object
  */
object Vega {

  /**
    * Create an empty plot object
    *
    * @param provider Provides the Vega dependency. Default is to use
    *                 the latest version from the JSdelivr CDN
    * @return Empty plot object
    */
  def apply(provider: VegaProvider = VegaLatestCDNVersion): Vega =
    new Vega(provider, Vector.empty)
}