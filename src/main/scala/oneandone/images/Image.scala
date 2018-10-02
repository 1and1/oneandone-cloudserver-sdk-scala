package oneandone.images

import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import oneandone.images.Frequency.Frequency
import oneandone.images.ImageType.ImageType
import oneandone.images.OsImageType.OsImageType
import oneandone.servers.GeneralState.GeneralState
import oneandone.servers.Hdds
import org.json4s.native.JsonMethods.parse
import org.json4s.{Extraction}

case class Image(
    id: String,
    name: String,
    datacenter: Datacenter,
    osFamily: String,
    os: String,
    osVersion: String,
    architecture: Double,
    osImageType: Option[OsImageType] = None,
    `type`: ImageType,
    minHddSize: Double,
    cloudpanelId: String,
    state: GeneralState,
    description: String,
    hdds: List[Hdds],
    serverId: Option[String] = None,
    frequency: Option[Frequency] = None,
    numImages: Option[Double] = None,
    creationDate: String
) {}

object ImageType extends Enumeration {
  type ImageType = Value
  val IMAGE       = Value("IMAGE")
  val MY_IMAGE    = Value("MY_IMAGE")
  val APPLICATION = Value("APPLICATION")
  val ISO         = Value("ISO")
}

object OsImageType extends Enumeration {
  type OsImageType = Value
  val STANDARD = Value("STANDARD")
  val MINIMAL  = Value("MINIMAL")
  val ISO_OS   = Value("ISO_OS")
  val ISO_TOOL = Value("ISO_TOOL")
}

object Frequency extends Enumeration {
  type Frequency = Value
  val ONCE   = Value("ONCE")
  val DAILY  = Value("DAILY")
  val WEEKLY = Value("WEEKLY")
}

object Image extends oneandone.Path {
  override val path: Seq[String] = Seq("images")

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[Image] = {
    val response = client.get(path, queryParameters)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[Image]]
  }

  def get(id: String)(implicit client: OneandoneClient): Image = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[Image]
  }

  def createImage(request: ImageRequest)(implicit client: OneandoneClient): Image = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Image]
  }

  def updateImage(id: String, request: UpdateImageRequest)(
      implicit client: OneandoneClient
  ): Image = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Image]
  }

  def delete(id: String)(implicit client: OneandoneClient): Image = {
    val response = client.delete(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[Image]
  }

  def waitImageStatus(id: String, status: String)(implicit client: OneandoneClient): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var img      = json.extract[Image]
    while (img.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      img = json.extract[Image]
    }
    true
  }
}
