package oneandone.prices

import oneandone.{BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods._


case class Price(
  currency: String,
  vat: Integer,
  pricingPlans: PricingPlans
) {}

object Price extends Path {
  val serializers                              = List(BooleanCustomSerializer)
  implicit lazy val serializerFormats: Formats = DefaultFormats ++ serializers
  override val path: Seq[String]               = Seq("pricing")

  def get()(implicit client: OneandoneClient): Price = {
    val response = client.get(path)
    val json     = parse(response).camelizeKeys
    json.extract[Price]
  }
}