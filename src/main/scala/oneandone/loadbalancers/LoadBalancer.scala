package oneandone.loadbalancers
import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import oneandone.firewallpolicies._
import oneandone.loadbalancers.Method.Method
import oneandone.servers.GeneralState.GeneralState
import org.json4s.Extraction
import org.json4s.native.JsonMethods.parse

case class LoadBalancer (
    id: String,
    name: String,
    state: GeneralState,
    creationDate: String,
    description: String,
    ip: String,
    healthCheckTest: String,
    healthCheckInterval: Double,
    healthCheckPath: String,
    healthCheckPathParser: String,
    persistence: Boolean,
    persistenceTime: Double,
    datacenter: Datacenter,
    method: Method,
    rules: Option[Seq[Rule]] = None,
    serverIps: Option[Seq[ServerIps]] = None
) {}

object Method extends Enumeration {
  type Method = Value
  val ROUND_ROBIN = Value("ROUND_ROBIN")
  val LEAST_CONNECTIONS  = Value("LEAST_CONNECTIONS")
}

object HealthCheckTest extends Enumeration {
  type HealthCheckTest = Value
  val NONE = Value("NONE")
  val TCP  = Value("TCP")
  val HTTP = Value("HTTP")
  val ICMP = Value("ICMP")
}

object LoadBalancer extends oneandone.Path {
  override val path: Seq[String] = Seq("load_balancers")
  var serversPath                = "server_ips"
  var rulesPath                  = "rules"

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[LoadBalancer] = {
    val response = client.get(path, queryParameters)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[LoadBalancer]]
  }

  def get(id: String)(implicit client: OneandoneClient): LoadBalancer = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[LoadBalancer]
  }

  def create(
      request: LoadbalancerRequest
  )(implicit client: OneandoneClient): LoadBalancer = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[LoadBalancer]
  }

  def update(id: String, request: UpdateLoadbalancerRequest)(
      implicit client: OneandoneClient
  ): LoadBalancer = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[LoadBalancer]
  }

  def delete(id: String)(implicit client: OneandoneClient): LoadBalancer = {
    val response = client.delete(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[LoadBalancer]
  }

  def listServerIps(
      loadbalancerId: String
  )(implicit client: OneandoneClient): Seq[ServerIps] = {
    val response = client.get(path :+ loadbalancerId :+ serversPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[ServerIps]]
  }

  def assignToServerIps(
      loadbalancerId: String,
      serverIps: Seq[String]
  )(implicit client: OneandoneClient): LoadBalancer = {

    var request = ("server_ips" -> serverIps)
    val response = client.post(
      path :+ loadbalancerId :+ serversPath,
      Extraction.decompose(request).snakizeKeys
    )
    val json = parse(response).camelizeKeys
    json.extract[LoadBalancer]
  }

  def unAssignServerIp(
      loadbalancerId: String,
      serverIpId: String
  )(implicit client: OneandoneClient): LoadBalancer = {
    val response = client.delete(path :+ loadbalancerId :+ serversPath :+ serverIpId)
    val json     = parse(response).camelizeKeys
    json.extract[LoadBalancer]
  }

  def getServerIp(
      loadbalancerId: String,
      serverIpId: String
  )(implicit client: OneandoneClient): ServerIps = {
    val response = client.get(path :+ loadbalancerId :+ serversPath :+ serverIpId)
    val json     = parse(response).camelizeKeys
    json.extract[ServerIps]
  }

  def listRules(
      loadbalancerId: String
  )(implicit client: OneandoneClient): Seq[Rule] = {
    val response = client.get(path :+ loadbalancerId :+ rulesPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[Rule]]
  }

  def assignRules(
      loadbalancerId: String,
      rules: Seq[RuleRequest]
  )(implicit client: OneandoneClient): LoadBalancer = {

    var request = ("rules" -> rules)
    val response = client.post(
      path :+ loadbalancerId :+ rulesPath,
      Extraction.decompose(request).snakizeKeys
    )
    val json = parse(response).camelizeKeys
    json.extract[LoadBalancer]
  }

  def getRule(
      loadbalancerId: String,
      ruleId: String
  )(implicit client: OneandoneClient): Rule = {
    val response = client.get(path :+ loadbalancerId :+ rulesPath :+ ruleId)
    val json     = parse(response).camelizeKeys
    json.extract[Rule]
  }

  def deleteRule(
      loadbalancerId: String,
      ruleId: String
  )(implicit client: OneandoneClient): LoadBalancer = {
    val response = client.delete(path :+ loadbalancerId :+ rulesPath :+ ruleId)
    val json     = parse(response).camelizeKeys
    json.extract[LoadBalancer]
  }

  def waitStatus(id: String, status: GeneralState)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var result   = json.extract[LoadBalancer]
    while (result.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      result = json.extract[LoadBalancer]
    }
    true
  }
}
