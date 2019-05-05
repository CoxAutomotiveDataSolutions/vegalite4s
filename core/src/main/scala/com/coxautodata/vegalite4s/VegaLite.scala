package com.coxautodata.vegalite4s

import com.coxautodata.vegalite4s.providers.{VegaLiteLatestCDNVersion, SchemaProvider, VegaLiteProvider}
import io.circe.JsonObject
import sun.net.NetHooks.Provider

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
               override val trans: Vector[JsonObject => JsonObject]) extends SchemaConstruct[VegaLite] {

  override def withObjectTransformation(t: JsonObject => JsonObject): VegaLite = new VegaLite(prov, trans :+ t)

  override def getProvider: SchemaProvider = prov
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
  def apply(provider: VegaLiteProvider = VegaLiteLatestCDNVersion): VegaLite =
    new VegaLite(provider, Vector.empty)
}