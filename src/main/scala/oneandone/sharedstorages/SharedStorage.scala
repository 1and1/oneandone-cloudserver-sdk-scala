package oneandone.sharedstorages
import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import oneandone.servers.GeneralState.GeneralState
import org.json4s.{Extraction}
import org.json4s.native.JsonMethods.parse

case class SharedStorage(
    id: String,
    size: Double,
    state: GeneralState,
    description: Option[String] = None,
    datacenter: Datacenter,
    sizeUsed: Option[Double] = None,
    cifsPath: Option[String] = None,
    nfspath: Option[String] = None,
    name: String,
    creationDate: String,
    servers: Option[List[SharedstorageServer]] = None
) {}

object SharedStorage extends oneandone.Path {
  override val path: Seq[String] = Seq("shared_storages")
  var ssServersPath              = "servers"
  var ssAccessPath               = "access"

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[SharedStorage] = {
    val response = client.get(path, queryParameters)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[SharedStorage]]
  }

  def get(id: String)(implicit client: OneandoneClient): SharedStorage = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[SharedStorage]
  }

  def create(
      request: SharedstorageRequest
  )(implicit client: OneandoneClient): SharedStorage = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[SharedStorage]
  }

  def update(id: String, request: UpdateSharedstorageRequest)(
      implicit client: OneandoneClient
  ): SharedStorage = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[SharedStorage]
  }

  def listAttachedServers(
      id: String
  )(implicit client: OneandoneClient): Seq[SharedstorageServer] = {
    val response = client.get(path :+ id :+ ssServersPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[SharedstorageServer]]
  }

  def attachServer(id: String, request: AttachServersRequest)(
      implicit client: OneandoneClient
  ): SharedStorage = {
    val response =
      client.post(path :+ id :+ ssServersPath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[SharedStorage]
  }

  def getAttachedServers(id: String, serverId: String)(
      implicit client: OneandoneClient
  ): Seq[SharedstorageServer] = {
    val response = client.get(path :+ id :+ ssServersPath :+ serverId)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[SharedstorageServer]]
  }

  def detachServer(id: String, serverId: String)(
      implicit client: OneandoneClient
  ): SharedStorage = {
    val response = client.delete(path :+ id :+ ssServersPath :+ serverId)
    val json     = parse(response).camelizeKeys
    json.extract[SharedStorage]
  }

  def getAccessSettings(id: String)(
      implicit client: OneandoneClient
  ): Seq[SharedstorageAccess] = {
    val response = client.get(path :+ ssAccessPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[SharedstorageAccess]]
  }

  def updatePassword(password: String)(
      implicit client: OneandoneClient
  ): Seq[SharedstorageAccess] = {

    var request  = ("password" -> password)
    val response = client.put(path :+ ssAccessPath, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[SharedstorageAccess]]
  }

  def delete(id: String)(implicit client: OneandoneClient) = {
    val response = client.delete(path :+ id)
  }

  def waitStatus(id: String, status: GeneralState)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var bs       = json.extract[SharedStorage]
    while (bs.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      bs = json.extract[SharedStorage]
    }
    true
  }
}
