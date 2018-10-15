package oneandone.blockstorages
import oneandone.OneandoneClient
import oneandone.blockstorages.StorageState.StorageState
import oneandone.datacenters.Datacenter
import oneandone.servers.GeneralState.{GeneralState}
import oneandone.servers.IdNameFields
import org.json4s.native.JsonMethods.parse
import org.json4s.{Extraction}

case class BlockStorage(
    id: String,
    size: Double,
    state: StorageState,
    description: String,
    datacenter: Datacenter,
    name: String,
    creationDate: String,
    server: Option[IdNameFields] = None
) {}

object StorageState extends Enumeration {
  type StorageState = Value
  val POWERED_ON = Value("POWERED_ON")
}

object BlockStorage extends oneandone.Path {
  override val path: Seq[String] = Seq("block_storages")
  var bsServersPath              = "server"

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[BlockStorage] = {
    val response = client.get(path, queryParameters)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[BlockStorage]]
  }

  def get(id: String)(implicit client: OneandoneClient): BlockStorage = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[BlockStorage]
  }

  def create(
      request: BlockstorageRequest
  )(implicit client: OneandoneClient): BlockStorage = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[BlockStorage]
  }

  def update(id: String, request: UpdateBlockstorageRequest)(
      implicit client: OneandoneClient
  ): BlockStorage = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[BlockStorage]
  }

  def attachServer(id: String, serverId: String)(implicit client: OneandoneClient): BlockStorage = {
    val request =
      ("server_id" ->
        serverId)
    val response =
      client.post(path :+ id :+ bsServersPath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[BlockStorage]
  }

  def detachServer(id: String)(implicit client: OneandoneClient): BlockStorage = {
    val response = client.delete(path :+ id :+ bsServersPath)
    val json     = parse(response).camelizeKeys
    json.extract[BlockStorage]
  }

  def getServer(id: String)(
      implicit client: OneandoneClient
  ): IdNameFields = {
    val response = client.get(path :+ id :+ bsServersPath)
    val json     = parse(response).camelizeKeys
    json.extract[IdNameFields]
  }

  def delete(id: String)(implicit client: OneandoneClient) = {
    val response = client.delete(path :+ id)
  }

  def waitStatus(id: String, status: StorageState)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var bs       = json.extract[BlockStorage]
    while (bs.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      bs = json.extract[BlockStorage]
    }
    true
  }
}
