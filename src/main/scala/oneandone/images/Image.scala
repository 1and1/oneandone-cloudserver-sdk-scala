package oneandone.images

import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import oneandone.servers.{Hdds}
import org.json4s.native.JsonMethods.parse
import org.json4s.{DefaultFormats, Extraction, Formats}

case class Image(
    id: String,
    name: String,
    datacenter: Datacenter,
    osFamily: String,
    os: String,
    osVersion: String,
    architecture: Double,
    osImageType: String,
    `type`: String,
    minHddSize: Double,
    cloudpanelId: String,
    state: String,
    description: String,
    hdds: List[Hdds],
    serverId: Option[String] = None,
    frequency: Option[String] = None,
    numImages: Option[Double] = None,
    creationDate: String
) {}

object Image extends oneandone.Path {
  override val path: Seq[String] = Seq("images")
  implicit lazy val serializerFormats: Formats = DefaultFormats

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[Image] = {
    val response = client.get(path, queryParameters)
    val json = parse(response).camelizeKeys
    json.extract[Seq[Image]]
  }

  def get(id: String)(implicit client: OneandoneClient): Image = {
    val response = client.get(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[Image]
  }

  def createImage(request: ImageRequest)(implicit client: OneandoneClient): Image = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Image]
  }

  def updateImage(id: String, request: UpdateImageRequest)(
      implicit client: OneandoneClient
  ): Image = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Image]
  }

  def delete(id: String)(implicit client: OneandoneClient): Image = {
    val response = client.delete(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[Image]
  }

  def waitImageStatus(id: String, status: String)(implicit client: OneandoneClient): Boolean = {
    var response = client.get(path :+ id)
    var json = parse(response).camelizeKeys
    var img = json.extract[Image]
    while (img.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      img = json.extract[Image]
    }
    true
  }
}
