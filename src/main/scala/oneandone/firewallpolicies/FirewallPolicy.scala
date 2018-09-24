package oneandone.firewallpolicies
import oneandone.OneandoneClient
import org.json4s.Extraction
import org.json4s.native.JsonMethods.parse

case class FirewallPolicy(
    id: String,
    name: String,
    description: String,
    state: String,
    creationDate: String,
    default: Option[Double] = None,
    rules: Option[List[Rule]] = None,
    serverIps: Option[Seq[ServerIps]] = None
) {}

object FirewallPolicy extends oneandone.Path {
  override val path: Seq[String] = Seq("firewall_policies")
  var serversPath = "server_ips"
  var rulesPath = "rules"

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[FirewallPolicy] = {
    val response = client.get(path, queryParameters)
    val json = parse(response).camelizeKeys
    json.extract[Seq[FirewallPolicy]]
  }

  def get(id: String)(implicit client: OneandoneClient): FirewallPolicy = {
    val response = client.get(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[FirewallPolicy]
  }

  def createFirewallPolicy(
      request: FirewallPolicyRequest
  )(implicit client: OneandoneClient): FirewallPolicy = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[FirewallPolicy]
  }

  def updateFirewallPolicy(id: String, request: UpdateFirewallPolicyRequest)(
      implicit client: OneandoneClient
  ): FirewallPolicy = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[FirewallPolicy]
  }

  def delete(id: String)(implicit client: OneandoneClient): FirewallPolicy = {
    val response = client.delete(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[FirewallPolicy]
  }

  def listServerIps(
      firewallPolicyId: String
  )(implicit client: OneandoneClient): Seq[ServerIps] = {
    val response = client.get(path :+ firewallPolicyId :+ serversPath)
    val json = parse(response).camelizeKeys
    json.extract[Seq[ServerIps]]
  }

  def assignToServerIps(
      firewallPolicyId: String,
      serverIps: Seq[String]
  )(implicit client: OneandoneClient): FirewallPolicy = {

    var request = ("server_ips" -> serverIps)
    val response = client.post(
      path :+ firewallPolicyId :+ serversPath,
      Extraction.decompose(request).snakizeKeys
    )
    val json = parse(response).camelizeKeys
    json.extract[FirewallPolicy]
  }

  def getServerIp(
      firewallPolicyId: String,
      serverIpId: String
  )(implicit client: OneandoneClient): ServerIps = {
    val response = client.get(path :+ firewallPolicyId :+ serversPath :+ serverIpId)
    val json = parse(response).camelizeKeys
    json.extract[ServerIps]
  }

  def listRules(
      firewallPolicyId: String
  )(implicit client: OneandoneClient): Seq[Rule] = {
    val response = client.get(path :+ firewallPolicyId :+ rulesPath)
    val json = parse(response).camelizeKeys
    json.extract[Seq[Rule]]
  }

  def assignRules(
      firewallPolicyId: String,
      rules: Seq[RuleRequest]
  )(implicit client: OneandoneClient): FirewallPolicy = {

    var request = ("rules" -> rules)
    val response = client.post(
      path :+ firewallPolicyId :+ rulesPath,
      Extraction.decompose(request).snakizeKeys
    )
    val json = parse(response).camelizeKeys
    json.extract[FirewallPolicy]
  }

  def getRule(
      firewallPolicyId: String,
      ruleId: String
  )(implicit client: OneandoneClient): Rule = {
    val response = client.get(path :+ firewallPolicyId :+ rulesPath :+ ruleId)
    val json = parse(response).camelizeKeys
    json.extract[Rule]
  }

  def deleteRule(
      firewallPolicyId: String,
      ruleId: String
  )(implicit client: OneandoneClient): FirewallPolicy = {
    val response = client.delete(path :+ firewallPolicyId :+ rulesPath :+ ruleId)
    val json = parse(response).camelizeKeys
    json.extract[FirewallPolicy]
  }

  def waitFirewallPolicyStatus(id: String, status: String)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json = parse(response).camelizeKeys
    var result = json.extract[FirewallPolicy]
    while (result.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      result = json.extract[FirewallPolicy]
    }
    true
  }
}
