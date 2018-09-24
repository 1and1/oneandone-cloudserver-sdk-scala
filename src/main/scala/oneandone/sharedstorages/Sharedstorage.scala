package oneandone.sharedstorages
import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import oneandone.servers.IdNameFields
import org.json4s.{DefaultFormats, Extraction, Formats}
import org.json4s.native.JsonMethods.parse

case class Sharedstorage(
    id: String,
    size: Double,
    state: String,
    description: Option[String] = None,
    datacenter: Datacenter,
    sizeUsed: Option[Double] = None,
    cifsPath: Option[String] = None,
    nfspath: Option[String] = None,
    name: String,
    creationDate: String,
    servers: Option[List[SharedstorageServer]] = None
) {}

object Sharedstorage extends oneandone.Path {
  override val path: Seq[String] = Seq("shared_storages")
  var ssServersPath = "servers"
  var ssAccessPath = "access"
  implicit lazy val serializerFormats: Formats = DefaultFormats

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[Sharedstorage] = {
    val response = client.get(path, queryParameters)
    val json = parse(response).camelizeKeys
    json.extract[Seq[Sharedstorage]]
  }

  def get(id: String)(implicit client: OneandoneClient): Sharedstorage = {
    val response = client.get(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[Sharedstorage]
  }

  def createSharedstorage(
      request: SharedstorageRequest
  )(implicit client: OneandoneClient): Sharedstorage = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Sharedstorage]
  }

  def updateSharedstorage(id: String, request: UpdateSharedstorageRequest)(
      implicit client: OneandoneClient
  ): Sharedstorage = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Sharedstorage]
  }

  def listAttachedServers(
      id: String
  )(implicit client: OneandoneClient): Seq[SharedstorageServer] = {
    val response = client.get(path :+ id :+ ssServersPath)
    val json = parse(response).camelizeKeys
    json.extract[Seq[SharedstorageServer]]
  }

  def attachServer(id: String, request: AttachServersRequest)(
      implicit client: OneandoneClient
  ): Sharedstorage = {
    val response =
      client.post(path :+ id :+ ssServersPath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Sharedstorage]
  }

  def getAttachedServers(id: String, serverId: String)(
      implicit client: OneandoneClient
  ): Seq[SharedstorageServer] = {
    val response = client.get(path :+ id :+ ssServersPath :+ serverId)
    val json = parse(response).camelizeKeys
    json.extract[Seq[SharedstorageServer]]
  }

  def detachServer(id: String, serverId: String)(
      implicit client: OneandoneClient
  ): Sharedstorage = {
    val response = client.delete(path :+ id :+ ssServersPath :+ serverId)
    val json = parse(response).camelizeKeys
    json.extract[Sharedstorage]
  }

  def getSharedstorageAccess(id: String)(
      implicit client: OneandoneClient
  ): Seq[SharedstorageAccess] = {
    val response = client.get(path :+ ssAccessPath)
    val json = parse(response).camelizeKeys
    json.extract[Seq[SharedstorageAccess]]
  }

  def updateSharedstoragePassword(password: String)(
      implicit client: OneandoneClient
  ): Seq[SharedstorageAccess] = {

    var request = ("password" -> password)
    val response = client.put(path :+ ssAccessPath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Seq[SharedstorageAccess]]
  }

  def delete(id: String)(implicit client: OneandoneClient) = {
    val response = client.delete(path :+ id)
  }

  def waitSharedstorageStatus(id: String, status: String)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json = parse(response).camelizeKeys
    var bs = json.extract[Sharedstorage]
    while (bs.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      bs = json.extract[Sharedstorage]
    }
    true
  }
}
