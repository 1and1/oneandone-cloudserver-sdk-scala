package oneandone.serverappliances

import oneandone.{BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods._

case class ServerAppliance(
  id: String,
  name: String,
  availableDatacenters: List[String],
  osFamily: Option[String],
  os: Option[String],
  osVersion: Option[String],
  osArchitecture: Option[String],
  osImageType: Option[String],
  `type`: Option[String],
  serverTypeCompatibility: Option[List[String]],
  minHddSize: Option[String],
  licenses: Option[List[String]],
  version: Option[String],
  categories: Option[List[String]]
) {}

object ServerAppliance extends Path {
  val serializers                              = List(BooleanCustomSerializer)
  implicit lazy val serializerFormats: Formats = DefaultFormats ++ serializers
  override val path: Seq[String]               = Seq("server_appliances")

  def list()(implicit client: OneandoneClient): Seq[ServerAppliance] = {
    val response = client.get(path)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[ServerAppliance]]
  }

  def get(id: String)(implicit client: OneandoneClient): ServerAppliance = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[ServerAppliance]
  }

}
