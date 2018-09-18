package oneandone.servers

import oneandone.OneandoneClient
import org.json4s.{DefaultFormats, Extraction, Formats}
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

import scala.concurrent.ExecutionContext

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
    dvd: String,
    snapshot: String,
    ips: Option[List[Ips]] = None,
    alerts: Option[String] = None,
    monitoringPolicy: Option[String] = None
) {}

object Server extends oneandone.Path {
  override val path: Seq[String]               = Seq("servers")
  val fixedInstancesPath                       = "fixed_instance_sizes"
  implicit lazy val serializerFormats: Formats = DefaultFormats

  def list()(implicit client: OneandoneClient): Seq[Server] = {
    val response = client.get(path)
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

  def getFixedInstances(id: String)(implicit client: OneandoneClient): FixedInstance = {
    val response = client.get(path :+ fixedInstancesPath :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[FixedInstance]
  }

  def createServer(request: Server)(implicit client: OneandoneClient): Server = {

    val response = client.post(path, Extraction.decompose(request))
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def apply(id: BigInt)(implicit client: OneandoneClient, ec: ExecutionContext) = {
    val path = this.path :+ id.toString
    for {
      response <- client.get[Server](path)
    } yield {
      response
    }
  }

//  def exists(
//      id: BigInt)(implicit client: OneandoneClient, ec: ExecutionContext): Future[Boolean] = {
//    val path = this.path :+ id.toString
//    client.exists(path)
//  }

//  def isDeleted(
//      id: BigInt)(implicit client: OneandoneClient, ec: ExecutionContext): Future[Boolean] = {
//    for {
//      exists <- exists(id)
//    } yield { !exists }
//  }

  def create(
      name: String,
      description: String,
      hardware: Hardware,
      applianceId: String,
      password: String,
      serverType: String,
      ipv6Range: String,
      hostname: String,
      powerOn: Boolean,
      firewallPolicyId: String,
      ipId: String,
      loadBalancerId: String,
      monitoringPolicyId: String,
      datacenterId: String,
      rsaKey: String,
      sshPassword: Boolean,
      publicKey: Seq[String],
      privateNetworkId: String
  )(implicit client: OneandoneClient, ec: ExecutionContext) = {
    var hardwareReq =
      ("baremetal_model_id"       -> hardware.baremetalModelId) ~
        ("fixed_instance_size_id" -> hardware.fixedInstanceSizeId) ~
        ("vcore"                  -> hardware.vCore) ~
        ("cores_per_processor"    -> hardware.coresPerProcessor) ~
        ("ram"                    -> hardware.ram)
//        ("hdds"                   -> hardware.hdds)
    val request =
      ("name"                   -> name) ~
        ("description"          -> description) ~
        ("hardware"             -> hardwareReq) ~
        ("appliance_id"         -> applianceId) ~
        ("password"             -> password) ~
        ("server_type"          -> serverType) ~
        ("ipv6_range"           -> ipv6Range) ~
        ("hostname"             -> hostname) ~
        ("power_on"             -> powerOn) ~
        ("firewall_policy_id"   -> firewallPolicyId) ~
        ("ip_id"                -> ipId) ~
        ("load_balancer_id"     -> loadBalancerId) ~
        ("monitoring_policy_id" -> monitoringPolicyId) ~
        ("datacenter_id"        -> datacenterId) ~
        ("rsa_key"              -> rsaKey) ~
        ("ssh_password"         -> sshPassword) ~
        ("public_key"           -> publicKey) ~
        ("private_network_id"   -> privateNetworkId)

//    for {
//      response <- client.post[Server](path, request)
//    } yield {
//      response
//    }
  }
}
