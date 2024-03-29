package oneandone
import oneandone.firewallpolicies._
import oneandone.servers.{FirewallPolicy => _, _}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class FirewallPolicyTest extends FunSuite with BeforeAndAfterAll {
  implicit val client                       = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var firewallPolicies: Seq[FirewallPolicy] = Seq.empty
  var fixedServer: Server                   = null
  val smallServerInstance: String           = "81504C620D98BCEBAA5202D145203B4B"
  var testFP: FirewallPolicy                = null
  var testRule: Rule                        = null
  var datacenters                           = oneandone.datacenters.Datacenter.list()

  override def beforeAll(): Unit = {
    super.beforeAll()
    var serverRequest = ServerRequest(
      "Scala firewall policy test",
      Some("desc"),
      Hardware(
        None,
        Some(smallServerInstance),
      ),
      "753E3C1F859874AA74EB63B3302601F5",
      Some(datacenters(0).id)
    )
    fixedServer = Server.createCloud(serverRequest)
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (fixedServer != null) {
      Server.delete(fixedServer.id)
      Server.waitDeleted(fixedServer.id)
    }
    if (testFP != null) {
      FirewallPolicy.waitStatus(testFP.id, GeneralState.ACTIVE)
      FirewallPolicy.delete(testFP.id)
    }
  }

  test("Create Firewall ") {

    var request = FirewallPolicyRequest(
      name = "test scala FP",
      rules = Seq[RuleRequest](
        RuleRequest(Protocol.TCP, "8080")
      )
    )

    testFP = FirewallPolicy.create(request)
    FirewallPolicy.waitStatus(testFP.id, GeneralState.ACTIVE)

  }

  test("List firewalls") {

    firewallPolicies = FirewallPolicy.list()
    assert(firewallPolicies.size > 0)
  }

  test("Get Firewall") {

    var result = FirewallPolicy.get(testFP.id)
    assert(result.id == testFP.id)
  }

  test("Update Firewall Policy") {
    var updateRequest = UpdateFirewallPolicyRequest(
      name = "new name scala test"
    )
    var bs = FirewallPolicy.update(testFP.id, updateRequest)
    assert(bs.name == "new name scala test")
    FirewallPolicy.waitStatus(testFP.id, GeneralState.ACTIVE)
  }

  test("Attach Firewall policy to server ip ") {

    fixedServer = Server.get(fixedServer.id)
    var request = Seq(fixedServer.ips.get(0).id)
    var result  = FirewallPolicy.assignToServerIps(testFP.id, request)
    FirewallPolicy.waitStatus(testFP.id, GeneralState.ACTIVE)
    assert(result.serverIps.get.size > 0)
  }

  test("List firewalls server ips") {

    var result = FirewallPolicy.listServerIps(testFP.id)
    assert(result.size > 0)
  }

  test("get firewalls server ip") {

    var result = FirewallPolicy.getServerIp(testFP.id, fixedServer.ips.get(0).id)
    assert(result.ip == fixedServer.ips.get(0).ip)
  }

  test("Add rule") {

    var request = Seq(RuleRequest(Protocol.TCP, "9000"))
    var result  = FirewallPolicy.assignRules(testFP.id, request)
    println(result.rules.get)
    testRule = result.rules.get(1)
    FirewallPolicy.waitStatus(testFP.id, GeneralState.ACTIVE)
    assert(result.rules.get.size > 1)
  }

  test("List firewalls rules") {

    var result = FirewallPolicy.listRules(testFP.id)
    assert(result.size > 0)
  }

  test("get firewalls rule") {

    var result = FirewallPolicy.getRule(testFP.id, testRule.id)
    assert(result.id == testRule.id)
  }

  test("delete firewalls rule") {

    var result = FirewallPolicy.deleteRule(testFP.id, testRule.id)
    assert(result.rules.get.size == 1)
  }
}
