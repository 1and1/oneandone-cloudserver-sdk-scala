package oneandone
import oneandone.loadbalancers._
import oneandone.servers._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class LoadbalancerTest extends FunSuite with BeforeAndAfterAll {
  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var loadBalancers: Seq[Loadbalancer] = Seq.empty
  var fixedServer: Server = null
  val smallServerInstance: String = "81504C620D98BCEBAA5202D145203B4B"
  var testLB: Loadbalancer = null
  var testRule: Rule = null
  var datacenters = oneandone.datacenters.Datacenter.list()

  override def beforeAll(): Unit = {
    super.beforeAll()
    var serverRequest = ServerRequest(
      "Scala load balancer test2",
      Some("desc"),
      Hardware(
        None,
        Some(smallServerInstance),
      ),
      "753E3C1F859874AA74EB63B3302601F5",
      Some(datacenters(0).id)
    )
    fixedServer = Server.createCloud(serverRequest)
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (fixedServer != null) {
      Server.delete(fixedServer.id)
      Server.waitServerDeleted(fixedServer.id)
    }
    if (testLB != null) {
      Loadbalancer.waitLoadbalancerStatus(testLB.id, GeneralState.ACTIVE)
      Loadbalancer.delete(testLB.id)
    }
  }

  test("Create LoadBalancer ") {

    var request = LoadbalancerRequest(
      name = "test scala LB",
      healthCheckTest = "TCP",
      healthCheckInterval = 40,
      persistence = true,
      persistenceTime = 1200,
      method = "ROUND_ROBIN",
      rules = Seq[RuleRequest](
        RuleRequest("TCP", 80, 80)
      ),
      datacenterId = Some(datacenters(0).id)
    )

    testLB = Loadbalancer.createLoadbalancer(request)
    Loadbalancer.waitLoadbalancerStatus(testLB.id, GeneralState.ACTIVE)

  }

  test("List loadBalancers") {

    loadBalancers = Loadbalancer.list()
    assert(loadBalancers.size > 0)
  }

  test("Get LoadBalancer") {

    var result = Loadbalancer.get(testLB.id)
    assert(result.id == testLB.id)
  }

  test("Update LoadBalancer ") {
    var updateRequest = UpdateLoadbalancerRequest(
      name = Some("new name scala test")
    )
    var bs = Loadbalancer.updateLoadbalancer(testLB.id, updateRequest)
    assert(bs.name == "new name scala test")
    Loadbalancer.waitLoadbalancerStatus(testLB.id, GeneralState.ACTIVE)
  }

  test("assign LoadBalancer  to server ip ") {

    fixedServer = Server.get(fixedServer.id)
    var request = Seq(fixedServer.ips.get(0).id)
    var result = Loadbalancer.assignToServerIps(testLB.id, request)
    Loadbalancer.waitLoadbalancerStatus(testLB.id, GeneralState.ACTIVE)
    assert(result.serverIps.get.size > 0)
  }

  test("List loadBalancers server ips") {

    var result = Loadbalancer.listServerIps(testLB.id)
    assert(result.size > 0)
  }

  test("get loadBalancers server ip") {

    var result = Loadbalancer.getServerIp(testLB.id, fixedServer.ips.get(0).id)
    assert(result.ip == fixedServer.ips.get(0).ip)
  }

  test("unassign loadBalancers server ip") {

    var result = Loadbalancer.unAssignServerIps(testLB.id, fixedServer.ips.get(0).id)
    assert(result.serverIps.get.size == 0)
  }

  test("Add rule") {

    var request = Seq(RuleRequest("TCP", 9000, 9000))
    var result = Loadbalancer.assignRules(testLB.id, request)
    println(result.rules.get)
    testRule = result.rules.get(1)
    Loadbalancer.waitLoadbalancerStatus(testLB.id, GeneralState.ACTIVE)
    assert(result.rules.get.size > 1)
  }

  test("List loadBalancers rules") {

    var result = Loadbalancer.listRules(testLB.id)
    assert(result.size > 0)
  }

  test("get loadBalancers rule") {

    var result = Loadbalancer.getRule(testLB.id, testRule.id)
    assert(result.id == testRule.id)
  }

  test("delete loadBalancers rule") {

    var result = Loadbalancer.deleteRule(testLB.id, testRule.id)
    assert(result.rules.get.size == 1)
  }
}
