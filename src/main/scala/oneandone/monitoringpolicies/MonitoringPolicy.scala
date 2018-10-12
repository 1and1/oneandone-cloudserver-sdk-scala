package oneandone.monitoringpolicies

import oneandone.servers.GeneralState.GeneralState
import org.json4s.native.JsonMethods._
import org.json4s.Extraction
import oneandone.{BasicResource, OneandoneClient, Path}

case class MonitoringPolicy(
    id: String,
    name: String,
    description: Option[String],
    default: Integer,
    state: GeneralState,
    creationDate: String,
    email: Option[String],
    agent: Boolean,
    servers: Seq[BasicResource],
    thresholds: Threshold,
    ports: Option[Seq[Port]],
    processes: Option[Seq[Process]],
    cloudpanelId: String
) {}

object MonitoringPolicy extends Path {
  override val path: Seq[String] = Seq("monitoring_policies")
  val PortsPath                  = "ports"
  val ProcessesPath              = "processes"
  val ServersPath                = "servers"

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[MonitoringPolicy] = {
    val response = client.get(path, queryParameters)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[MonitoringPolicy]]
  }

  def get(id: String)(implicit client: OneandoneClient): MonitoringPolicy = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def create(
      request: MonitoringPolicyRequest
  )(implicit client: OneandoneClient): MonitoringPolicy = {
    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def update(
      id: String,
      request: MonitoringPolicyRequest
  )(implicit client: OneandoneClient): MonitoringPolicy = {
    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def delete(id: String)(implicit client: OneandoneClient): Seq[String] = {
    val response = client.delete(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[String]]
  }

  def listPorts(id: String)(implicit client: OneandoneClient): Seq[Port] = {
    val response = client.get(path :+ id :+ PortsPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[Port]]
  }

  def addPorts(id: String, request: AddPortsRequest)(
      implicit client: OneandoneClient
  ): MonitoringPolicy = {
    val response = client.post(path :+ id :+ PortsPath, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def getPort(monitoringPolicyId: String, portId: String)(
      implicit client: OneandoneClient
  ): Port = {
    val response = client.get(path :+ monitoringPolicyId :+ PortsPath :+ portId)
    val json     = parse(response).camelizeKeys
    json.extract[Port]
  }

  def updatePort(monitoringPolicyId: String, portId: String, request: UpdatePortRequest)(
      implicit client: OneandoneClient
  ): MonitoringPolicy = {
    val response = client.put(
      path :+ monitoringPolicyId :+ PortsPath :+ portId,
      Extraction.decompose(request).snakizeKeys
    )
    val json = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def deletePort(monitoringPolicyId: String, portId: String)(
      implicit client: OneandoneClient
  ): MonitoringPolicy = {
    val response = client.delete(path :+ monitoringPolicyId :+ PortsPath :+ portId)
    val json     = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def listProcesses(id: String)(implicit client: OneandoneClient): Seq[Process] = {
    val response = client.get(path :+ id :+ ProcessesPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[Process]]
  }

  def addProccesses(id: String, request: AddProcessesRequest)(
      implicit client: OneandoneClient
  ): MonitoringPolicy = {
    val response =
      client.post(path :+ id :+ ProcessesPath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def getProcess(monitoringPolicyId: String, processId: String)(
      implicit client: OneandoneClient
  ): Process = {
    val response = client.get(path :+ monitoringPolicyId :+ ProcessesPath :+ processId)
    val json     = parse(response).camelizeKeys
    json.extract[Process]
  }

  def updateProcess(monitoringPolicyId: String, processId: String, request: UpdateProcessRequest)(
      implicit client: OneandoneClient
  ): MonitoringPolicy = {
    val response = client.put(
      path :+ monitoringPolicyId :+ ProcessesPath :+ processId,
      Extraction.decompose(request).snakizeKeys
    )
    val json = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def deleteProcess(monitoringPolicyId: String, processId: String)(
      implicit client: OneandoneClient
  ): MonitoringPolicy = {
    val response = client.delete(path :+ monitoringPolicyId :+ ProcessesPath :+ processId)
    val json     = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def addServers(id: String, request: AddServersRequest)(
      implicit client: OneandoneClient
  ): MonitoringPolicy = {
    val response =
      client.post(path :+ id :+ ServersPath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def listServers(id: String)(
      implicit client: OneandoneClient
  ): Seq[BasicResource] = {
    val response = client.get(path :+ id :+ ServersPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[BasicResource]]
  }

  def getServer(monitoringPolicyId: String, serverId: String)(
      implicit client: OneandoneClient
  ): BasicResource = {
    val response = client.get(path :+ monitoringPolicyId :+ ServersPath :+ serverId)
    val json     = parse(response).camelizeKeys
    json.extract[BasicResource]
  }

  def deleteServer(monitoringPolicyId: String, serverId: String)(
      implicit client: OneandoneClient
  ): MonitoringPolicy = {
    val response = client.delete(path :+ monitoringPolicyId :+ ServersPath :+ serverId)
    val json     = parse(response).camelizeKeys
    json.extract[MonitoringPolicy]
  }

  def waitMonitoringPolicyStatus(id: String, status: GeneralState)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var result   = json.extract[MonitoringPolicy]
    while (result.state != status) {
      Thread.sleep(1000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      result = json.extract[MonitoringPolicy]
    }
    true
  }
}
