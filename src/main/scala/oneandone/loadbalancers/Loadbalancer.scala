package oneandone.loadbalancers
import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import oneandone.firewallpolicies._
import oneandone.servers.GeneralState.GeneralState
import org.json4s.Extraction
import org.json4s.native.JsonMethods.parse

case class Loadbalancer(
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
    method: String,
    rules: Option[Seq[Rule]] = None,
    serverIps: Option[Seq[ServerIps]] = None
) {}

object Loadbalancer extends oneandone.Path {
  override val path: Seq[String] = Seq("load_balancers")
  var serversPath                = "server_ips"
  var rulesPath                  = "rules"

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[Loadbalancer] = {
    val response = client.get(path, queryParameters)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[Loadbalancer]]
  }

  def get(id: String)(implicit client: OneandoneClient): Loadbalancer = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[Loadbalancer]
  }

  def createLoadbalancer(
      request: LoadbalancerRequest
  )(implicit client: OneandoneClient): Loadbalancer = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Loadbalancer]
  }

  def updateLoadbalancer(id: String, request: UpdateLoadbalancerRequest)(
      implicit client: OneandoneClient
  ): Loadbalancer = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Loadbalancer]
  }

  def delete(id: String)(implicit client: OneandoneClient): Loadbalancer = {
    val response = client.delete(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[Loadbalancer]
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
  )(implicit client: OneandoneClient): Loadbalancer = {

    var request = ("server_ips" -> serverIps)
    val response = client.post(
      path :+ loadbalancerId :+ serversPath,
      Extraction.decompose(request).snakizeKeys
    )
    val json = parse(response).camelizeKeys
    json.extract[Loadbalancer]
  }

  def unAssignServerIps(
      loadbalancerId: String,
      serverIpId: String
  )(implicit client: OneandoneClient): Loadbalancer = {
    val response = client.delete(path :+ loadbalancerId :+ serversPath :+ serverIpId)
    val json     = parse(response).camelizeKeys
    json.extract[Loadbalancer]
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
  )(implicit client: OneandoneClient): Loadbalancer = {

    var request = ("rules" -> rules)
    val response = client.post(
      path :+ loadbalancerId :+ rulesPath,
      Extraction.decompose(request).snakizeKeys
    )
    val json = parse(response).camelizeKeys
    json.extract[Loadbalancer]
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
  )(implicit client: OneandoneClient): Loadbalancer = {
    val response = client.delete(path :+ loadbalancerId :+ rulesPath :+ ruleId)
    val json     = parse(response).camelizeKeys
    json.extract[Loadbalancer]
  }

  def waitLoadbalancerStatus(id: String, status: GeneralState)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var result   = json.extract[Loadbalancer]
    while (result.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      result = json.extract[Loadbalancer]
    }
    true
  }
}
