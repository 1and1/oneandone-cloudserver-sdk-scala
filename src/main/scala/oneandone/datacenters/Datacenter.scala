package oneandone.datacenters
import oneandone.{BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods._

case class Datacenter(
    id: String,
    location: String,
    countryCode: String,
    default: Option[Boolean]
) {}

object Datacenter extends Path {
  val serializers = List(BooleanCustomSerializer)
  override implicit lazy val serializerFormats: Formats = DefaultFormats ++ serializers
  override val path: Seq[String] = Seq("datacenters")

  def list()(implicit client: OneandoneClient): Seq[Datacenter] = {
    val response = client.get(path)
    val json = parse(response).camelizeKeys
    json.extract[Seq[Datacenter]]
  }

  def get(id: String)(implicit client: OneandoneClient): Datacenter = {
    val response = client.get(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[Datacenter]
  }

}
