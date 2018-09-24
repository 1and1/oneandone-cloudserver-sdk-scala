package oneandone.blockstorages
import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import oneandone.servers.IdNameFields
import org.json4s.native.JsonMethods.parse
import org.json4s.{DefaultFormats, Extraction, Formats}

case class Blockstorage(
    id: String,
    size: Double,
    state: String,
    description: String,
    datacenter: Datacenter,
    name: String,
    creationDate: String,
    server: Option[IdNameFields] = None
) {}

object Blockstorage extends oneandone.Path {
  override val path: Seq[String] = Seq("block_storages")
  var bsServersPath = "server"

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[Blockstorage] = {
    val response = client.get(path, queryParameters)
    val json = parse(response).camelizeKeys
    json.extract[Seq[Blockstorage]]
  }

  def get(id: String)(implicit client: OneandoneClient): Blockstorage = {
    val response = client.get(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[Blockstorage]
  }

  def createBlockstorage(
      request: BlockstorageRequest
  )(implicit client: OneandoneClient): Blockstorage = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Blockstorage]
  }

  def updateBlockstorage(id: String, request: UpdateBlockstorageRequest)(
      implicit client: OneandoneClient
  ): Blockstorage = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Blockstorage]
  }

  def attachServer(id: String, serverId: String)(implicit client: OneandoneClient): Blockstorage = {
    val request =
    ("server_id" ->
    serverId)
    val response =
      client.post(path :+ id :+ bsServersPath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Blockstorage]
  }

  def detachServer(id: String)(implicit client: OneandoneClient): Blockstorage = {
    val response = client.delete(path :+ id :+ bsServersPath)
    val json = parse(response).camelizeKeys
    json.extract[Blockstorage]
  }

  def getServer(id: String)(
      implicit client: OneandoneClient
  ): IdNameFields = {
    val response = client.get(path :+ id :+ bsServersPath)
    val json = parse(response).camelizeKeys
    json.extract[IdNameFields]
  }

  def delete(id: String)(implicit client: OneandoneClient) = {
    val response = client.delete(path :+ id)
  }

  def waitBlockstorageStatus(id: String, status: String)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json = parse(response).camelizeKeys
    var bs = json.extract[Blockstorage]
    while (bs.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      bs = json.extract[Blockstorage]
    }
    true
  }
}
