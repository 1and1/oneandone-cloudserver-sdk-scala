package oneandone.ping

import oneandone.{BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods._


case class Ping(
  value: List[String]
) {}

object Ping extends Path {
  val serializers                              = List(BooleanCustomSerializer)
  override implicit lazy val serializerFormats: Formats = DefaultFormats ++ serializers
  override val path: Seq[String]               = Seq("ping")

  def get()(implicit client: OneandoneClient): Ping = {
    val response = "{\"value\": " + client.get(path) + "}"

    val json     = parse(response).camelizeKeys
    json.extract[Ping]
  }
}