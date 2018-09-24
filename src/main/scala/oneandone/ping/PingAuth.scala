package oneandone.ping

import oneandone.{BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods._

case class PingAuth(
  value: List[String]
) {}

object PingAuth extends Path {
  val serializers                              = List(BooleanCustomSerializer)
  implicit lazy val serializerFormats: Formats = DefaultFormats ++ serializers
  override val path: Seq[String]               = Seq("ping_auth")

  def get()(implicit client: OneandoneClient): PingAuth = {
    val response = "{\"value\": " + client.get(path) + "}"

    val json     = parse(response).camelizeKeys
    json.extract[PingAuth]
  }
}