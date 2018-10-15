package oneandone.privatenetworks
import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import oneandone.servers.GeneralState.GeneralState
import oneandone.servers.IdNameFields
import org.json4s.Extraction
import org.json4s.native.JsonMethods.parse

case class PrivateNetwork(
    id: String,
    name: String,
    description: Option[String] = None,
    networkAddress: String,
    subnetMask: String,
    state: GeneralState,
    datacenter: Option[Datacenter] = None,
    creationDate: Option[String] = None,
    servers: Option[Seq[IdNameFields]] = None,
) {}

object PrivateNetwork extends oneandone.Path {
  override val path: Seq[String] = Seq("private_networks")
  var pnServersPath              = "servers"

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[PrivateNetwork] = {
    val response = client.get(path, queryParameters)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[PrivateNetwork]]
  }

  def get(id: String)(implicit client: OneandoneClient): PrivateNetwork = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[PrivateNetwork]
  }

  def create(
      request: PrivateNetworkRequest
  )(implicit client: OneandoneClient): PrivateNetwork = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[PrivateNetwork]
  }

  def update(id: String, request: UpdatePrivateNetworkRequest)(
      implicit client: OneandoneClient
  ): PrivateNetwork = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[PrivateNetwork]
  }

  def attachServer(id: String, servers: Seq[String])(
      implicit client: OneandoneClient
  ): PrivateNetwork = {
    val request =
      ("servers" ->
        servers)
    val response =
      client.post(path :+ id :+ pnServersPath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[PrivateNetwork]
  }

  def detachServer(id: String, serverId: String)(
      implicit client: OneandoneClient
  ): PrivateNetwork = {
    val response = client.delete(path :+ id :+ pnServersPath :+ serverId)
    val json     = parse(response).camelizeKeys
    json.extract[PrivateNetwork]
  }

  def listServers(id: String)(
      implicit client: OneandoneClient
  ): Seq[IdNameFields] = {
    val response = client.get(path :+ id :+ pnServersPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[IdNameFields]]
  }

  def getServer(id: String, serverId: String)(
      implicit client: OneandoneClient
  ): IdNameFields = {
    val response = client.get(path :+ id :+ pnServersPath :+ serverId)
    val json     = parse(response).camelizeKeys
    json.extract[IdNameFields]
  }

  def delete(id: String)(implicit client: OneandoneClient) = {
    val response = client.delete(path :+ id)
  }

  def waitStatus(id: String, status: GeneralState)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var bs       = json.extract[PrivateNetwork]
    while (bs.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      bs = json.extract[PrivateNetwork]
    }
    true
  }
}
