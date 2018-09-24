package oneandone.recoveryappliances
import oneandone.{BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods._

case class RecoveryAppliance(
  id: String,
  name: String,
  os: Option[String],
  osFamily: Option[String],
  osVersion: Option[String],
  osArchitecture: Option[String],
  availableDatacenters: List[String]
) {}

object RecoveryAppliance extends Path {
  val serializers                              = List(BooleanCustomSerializer)
  implicit lazy val serializerFormats: Formats = DefaultFormats ++ serializers
  override val path: Seq[String]               = Seq("recovery_appliances")

  def list()(implicit client: OneandoneClient): Seq[RecoveryAppliance] = {
    val response = client.get(path)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[RecoveryAppliance]]
  }

  def get(id: String)(implicit client: OneandoneClient): RecoveryAppliance = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[RecoveryAppliance]
  }

}
