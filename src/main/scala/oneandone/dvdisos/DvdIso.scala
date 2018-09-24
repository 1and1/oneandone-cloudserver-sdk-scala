package oneandone.dvdisos

import oneandone.{BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods._

case class DvdIso(
    id: String,
    name: String,
    availableDatacenters: List[String],
    osFamily: Option[String],
    os: Option[String],
    osVersion: Option[String],
    osArchitecture: Option[String],
    osImageType: Option[String],
    `type`: Option[String],
    minHddSize: Option[String],
    licenses: Option[List[String]],
    categories: Option[List[String]],
    serverTypeCompatibility: Option[List[String]]
) {}

object DvdIso extends Path {
  val serializers                              = List(BooleanCustomSerializer)
  implicit lazy val serializerFormats: Formats = DefaultFormats ++ serializers
  override val path: Seq[String]               = Seq("dvd_isos")

  def list()(implicit client: OneandoneClient): Seq[DvdIso] = {
    val response = client.get(path)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[DvdIso]]
  }

  def get(id: String)(implicit client: OneandoneClient): DvdIso = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[DvdIso]
  }

}
