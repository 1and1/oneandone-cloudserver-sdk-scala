package oneandone.publicips
import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import oneandone.publicips.AssignedToType.AssignedToType
import oneandone.publicips.IPType.IPType
import oneandone.servers.GeneralState.GeneralState
import org.json4s.Extraction
import org.json4s.native.JsonMethods.parse

case class PublicIp(
    id: String,
    ip: String,
    `type`: IPType,
    datacenter: Datacenter,
    assignedTo: Option[AssignedTo] = None,
    reverseDns: Option[String] = None,
    isDhcp: Boolean,
    state: GeneralState,
    creationDate: String
) {}

case class AssignedTo(
    id: String,
    name: String,
    `type`: AssignedToType,
) {}

object IPType extends Enumeration {
  type IPType = Value
  val IPV4 = Value("IPV4")
  val IPV6 = Value("IPV6")
}

object AssignedToType extends Enumeration {
  type AssignedToType = Value
  val SERVER        = Value("SERVER")
  val LOAD_BALANCER = Value("LOAD_BALANCER")
}

object PublicIp extends oneandone.Path {
  override val path: Seq[String] = Seq("public_ips")

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[PublicIp] = {
    val response = client.get(path, queryParameters)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[PublicIp]]
  }

  def get(id: String)(implicit client: OneandoneClient): PublicIp = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[PublicIp]
  }

  def createPublicIp(request: PublicIpRequest)(implicit client: OneandoneClient): PublicIp = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[PublicIp]
  }

  def updatePublicIp(id: String, reverseDns: String)(
      implicit client: OneandoneClient
  ): PublicIp = {
    var request  = ("reverse_dns" -> reverseDns)
    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[PublicIp]
  }

  def delete(id: String)(implicit client: OneandoneClient): PublicIp = {
    val response = client.delete(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[PublicIp]
  }

  def waitPublicIpStatus(id: String, status: GeneralState)(
      implicit client: OneandoneClient): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var ip       = json.extract[PublicIp]
    while (ip.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      ip = json.extract[PublicIp]
    }
    true
  }
}
