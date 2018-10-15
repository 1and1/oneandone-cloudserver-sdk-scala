package oneandone.servers

import oneandone.OneandoneClient
import oneandone.privatenetworks.PrivateNetwork
import oneandone.servers.ActionMethod.ActionMethod
import oneandone.servers.ServerAction.ServerAction
import oneandone.servers.ServerState.ServerState
import org.json4s.Extraction
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

case class Server(
    id: String,
    name: String,
    description: String,
    serverType: String,
    datacenter: Datacenter,
    creationDate: String,
    firstPassword: String,
    status: Status,
    hardware: Hardware,
    image: Image,
    dvd: Option[IdNameFields] = None,
    snapshot: Option[Snapshots] = None,
    ips: Option[List[Ips]] = None,
    alerts: Option[String] = None,
    monitoringPolicy: Option[String] = None,
    privateNetworks: Option[List[ServerPrivateNetwork]] = None
) {}

object Server extends oneandone.Path {
  override val path: Seq[String] = Seq("servers")
  val fixedInstancesPath         = "fixed_instance_sizes"
  val baremetalModelPath         = "baremetal_models"
  val hardwarePath               = "hardware"
  val hddsPath                   = "hardware/hdds"
  val serverImagePath            = "image"
  val serverIpsPath              = "ips"
  val serverFirewallPolicyPath   = "firewall_policy"
  val serverLoadBalancerPath     = "load_balancers"
  var serverStatusPath           = "status"
  var serverDvd                  = "dvd"
  var privateNetworksPath        = "private_networks"
  var snapshotsPath              = "snapshots"

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[Server] = {
    val response = client.get(path, queryParameters)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[Server]]
  }

  def get(id: String)(implicit client: OneandoneClient): Server = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def listFixedInstances()(implicit client: OneandoneClient): Seq[FixedInstance] = {
    val response = client.get(path :+ fixedInstancesPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[FixedInstance]]
  }

  def getFixedInstance(id: String)(implicit client: OneandoneClient): FixedInstance = {
    val response = client.get(path :+ fixedInstancesPath :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[FixedInstance]
  }

  def listBaremetalModels(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[BaremetalModel] = {
    val response = client.get(path :+ baremetalModelPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[BaremetalModel]]
  }

  def getBaremetalModel(id: String)(implicit client: OneandoneClient): BaremetalModel = {
    val response = client.get(path :+ baremetalModelPath :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[BaremetalModel]
  }

  def createCloud(request: ServerRequest)(implicit client: OneandoneClient): Server = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def createBaremetal(request: BaremetalServerRequest)(implicit client: OneandoneClient): Server = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def modifyInformation(id: String, name: String, description: String)(
      implicit client: OneandoneClient
  ): Server = {
    val request =
      ("name"          -> name) ~
        ("description" -> description)

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def getHardware(id: String)(implicit client: OneandoneClient): Hardware = {
    val response = client.get(path :+ id :+ hardwarePath)
    val json     = parse(response).camelizeKeys
    json.extract[Hardware]
  }

  def updateHardware(id: String, request: UpdateHardwareRequest)(
      implicit client: OneandoneClient
  ): Server = {
    val response = client.put(path :+ id :+ hardwarePath, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def getHdds(id: String)(implicit client: OneandoneClient): Seq[Hdds] = {
    val response = client.get(path :+ id :+ hddsPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[Hdds]]
  }

  def addHdd(id: String, requestBody: Seq[HddRequest])(
      implicit client: OneandoneClient
  ): Server = {
    var request =
      ("hdds" -> requestBody)

    val response = client.post(path :+ id :+ hddsPath, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def getSingleHdd(id: String, hddId: String)(implicit client: OneandoneClient): Hdds = {
    val response = client.get(path :+ id :+ hddsPath :+ hddId)
    val json     = parse(response).camelizeKeys
    json.extract[Hdds]
  }

  def updateSingleHdd(id: String, hddId: String, newSize: Int)(
      implicit client: OneandoneClient
  ): Server = {
    val request =
      ("size" -> newSize)
    val response =
      client.put(path :+ id :+ hddsPath :+ hddId, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def getImage(id: String)(implicit client: OneandoneClient): Image = {
    val response = client.get(path :+ id :+ serverImagePath)
    val json     = parse(response).camelizeKeys
    json.extract[Image]
  }

  def reinstallImage(id: String, request: ReinstallImageRequest)(
      implicit client: OneandoneClient
  ): Server = {
    val response =
      client.put(path :+ id :+ serverImagePath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def listIps(id: String)(implicit client: OneandoneClient): Seq[Ips] = {
    val response = client.get(path :+ id :+ serverIpsPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[Ips]]
  }

  def addNewIP(id: String, protocol: String)(implicit client: OneandoneClient): Server = {
    var request =
      ("type" -> protocol)
    val response =
      client.post(path :+ id :+ serverIpsPath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def getIp(id: String, ipId: String)(implicit client: OneandoneClient): Ips = {
    val response = client.get(path :+ id :+ serverIpsPath :+ ipId)
    val json     = parse(response).camelizeKeys
    json.extract[Ips]
  }

  def getIpsFirewallPolicy(id: String, ipId: String)(
      implicit client: OneandoneClient
  ): FirewallPolicy = {
    val response = client.get(path :+ id :+ serverIpsPath :+ ipId :+ serverFirewallPolicyPath)
    val json     = parse(response).camelizeKeys
    json.extract[FirewallPolicy]
  }

  def addFirewallPolicy(id: String, ipId: String, fpId: String)(
      implicit client: OneandoneClient
  ): Server = {
    val request =
      ("id" -> fpId)
    val response =
      client.put(
        path :+ id :+ serverIpsPath :+ ipId :+ serverFirewallPolicyPath,
        Extraction.decompose(request).snakizeKeys
      )
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def getIpsLoadBalancers(id: String, ipId: String)(
      implicit client: OneandoneClient
  ): Seq[LoadBalancer] = {
    val response = client.get(path :+ id :+ serverIpsPath :+ ipId :+ serverLoadBalancerPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[LoadBalancer]]
  }

  def addLoadBalancer(id: String, ipId: String, lbId: String)(
      implicit client: OneandoneClient
  ): Server = {
    val request =
      ("load_balancer_id" -> lbId)
    val response =
      client.post(
        path :+ id :+ serverIpsPath :+ ipId :+ serverLoadBalancerPath,
        Extraction.decompose(request).snakizeKeys
      )
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def unassignLoadBalancer(id: String, ipId: String, lbId: String)(
      implicit client: OneandoneClient): Server = {

    val response =
      client.delete(path :+ id :+ serverIpsPath :+ ipId :+ serverLoadBalancerPath :+ lbId)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def getStatus(id: String)(implicit client: OneandoneClient): Status = {
    val response = client.get(path :+ id :+ serverStatusPath)
    val json     = parse(response).camelizeKeys
    json.extract[Status]
  }

  def getDvd(id: String)(implicit client: OneandoneClient): IdNameFields = {
    val response = client.get(path :+ id :+ serverDvd)
    val json     = parse(response).camelizeKeys
    json.extract[IdNameFields]
  }

  def updateStatus(id: String, action: ServerAction, method: ActionMethod)(
      implicit client: OneandoneClient
  ): Server = {
    val request =
      ("action"   -> action.toString) ~
        ("method" -> method.toString)

    val response =
      client.put(
        path :+ id :+ serverStatusPath :+ "action",
        Extraction.decompose(request).snakizeKeys
      )
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def loadDvd(id: String, dvdId: String)(implicit client: OneandoneClient): Server = {
    val request =
      ("id" -> dvdId)

    val response =
      client.put(path :+ id :+ serverDvd, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def unloadDvd(id: String)(implicit client: OneandoneClient): Server = {

    val response =
      client.delete(path :+ id :+ serverDvd)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def listPrivateNetwork(id: String)(
      implicit client: OneandoneClient): Seq[ServerPrivateNetwork] = {
    val response = client.get(path :+ id :+ privateNetworksPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[ServerPrivateNetwork]]
  }

  def getPrivateNetwork(id: String, pnId: String)(
      implicit client: OneandoneClient
  ): PrivateNetwork = {
    val response = client.get(path :+ id :+ privateNetworksPath :+ pnId)
    val json     = parse(response).camelizeKeys
    json.extract[PrivateNetwork]
  }

  def assignPrivateNetwork(id: String, pnId: String)(implicit client: OneandoneClient): Server = {
    val request =
      ("id" -> pnId)
    val response =
      client.post(path :+ id :+ privateNetworksPath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def getSnapshots(id: String)(implicit client: OneandoneClient): Snapshots = {
    val response = client.get(path :+ id :+ snapshotsPath)
    val json     = parse(response).camelizeKeys
    json.extract[Snapshots]
  }

  def createSnapshot(id: String)(implicit client: OneandoneClient): Server = {
    val response =
      client.post(path :+ id :+ snapshotsPath, Extraction.decompose(null))
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def restoreSnapshot(id: String, snapshotId: String)(implicit client: OneandoneClient): Server = {
    val response =
      client.put(path :+ id :+ snapshotsPath :+ snapshotId, Extraction.decompose(null))
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def clone(id: String, request: CloneServerRequest)(
      implicit client: OneandoneClient
  ): Server = {
    val response =
      client.post(path :+ id :+ "clone", Extraction.decompose(request))
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def deleteSnapshot(id: String, snapshotId: String)(implicit client: OneandoneClient): Server = {

    val response =
      client.delete(path :+ id :+ snapshotsPath :+ snapshotId)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def deletePrivateNetwork(id: String, pnId: String)(implicit client: OneandoneClient): Server = {

    val response =
      client.delete(path :+ id :+ privateNetworksPath :+ pnId)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def deleteIp(id: String, ipId: String)(implicit client: OneandoneClient): Server = {

    val response =
      client.delete(path :+ id :+ serverIpsPath :+ ipId)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def deleteSingleHdd(id: String, hddId: String)(implicit client: OneandoneClient): Server = {

    val response =
      client.delete(path :+ id :+ hddsPath :+ hddId)
    val json = parse(response).camelizeKeys
    json.extract[Server]
  }

  def delete(id: String)(implicit client: OneandoneClient): Server = {
    val response = client.delete(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def waitStatus(id: String, status: ServerState)(
      implicit client: OneandoneClient): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var srvr     = json.extract[Server]
    while (srvr.status.state.toString != status.toString) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      srvr = json.extract[Server]
    }
    true
  }

  //this method is required when some operations shows the correct status but the percentage is not 0 or null
  def waitStatusAndPercentage(id: String, status: ServerState)(
      implicit client: OneandoneClient): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var srvr     = json.extract[Server]
    while (srvr.status.state.toString != status.toString || srvr.status.percent != None) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      srvr = json.extract[Server]
    }
    true
  }

  def waitDeleted(
      id: String
  )(implicit client: OneandoneClient): Boolean = {
    val path = this.path :+ id
    try {
      while (true) {
        Thread.sleep(10000)
        client.get(path)
      }
    } catch {
      case e: oneandone.NotFoundExcepetion =>
        false
    }
    true
  }

}
