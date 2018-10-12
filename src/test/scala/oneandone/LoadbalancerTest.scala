package oneandone
import oneandone.loadbalancers._
import oneandone.servers.{LoadBalancer => _, _}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class LoadbalancerTest extends FunSuite with BeforeAndAfterAll {
  implicit val client                  = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var loadBalancers: Seq[LoadBalancer] = Seq.empty
  var fixedServer: Server              = null
  val smallServerInstance: String      = "81504C620D98BCEBAA5202D145203B4B"
  var testLB: LoadBalancer             = null
  var testRule: Rule                   = null
  var datacenters                      = oneandone.datacenters.Datacenter.list()

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
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (fixedServer != null) {
      Server.delete(fixedServer.id)
      Server.waitDeleted(fixedServer.id)
    }
    if (testLB != null) {
      LoadBalancer.waitStatus(testLB.id, GeneralState.ACTIVE)
      LoadBalancer.delete(testLB.id)
    }
  }

  test("Create LoadBalancer ") {

    var request = LoadbalancerRequest(
      name = "test scala LB",
      healthCheckTest = HealthCheckTest.TCP,
      healthCheckInterval = 40,
      persistence = true,
      persistenceTime = 1200,
      method = Method.ROUND_ROBIN,
      rules = Seq[RuleRequest](
        RuleRequest(LoadBalancerProtocol.TCP, 80, 80)
      ),
      datacenterId = Some(datacenters(0).id)
    )

    testLB = LoadBalancer.create(request)
    LoadBalancer.waitStatus(testLB.id, GeneralState.ACTIVE)

  }

  test("List loadBalancers") {

    loadBalancers = LoadBalancer.list()
    assert(loadBalancers.size > 0)
  }

  test("Get LoadBalancer") {

    var result = LoadBalancer.get(testLB.id)
    assert(result.id == testLB.id)
  }

  test("Update LoadBalancer ") {
    var updateRequest = UpdateLoadbalancerRequest(
      name = Some("new name scala test")
    )
    var bs = LoadBalancer.update(testLB.id, updateRequest)
    assert(bs.name == "new name scala test")
    LoadBalancer.waitStatus(testLB.id, GeneralState.ACTIVE)
  }

  test("assign LoadBalancer  to server ip ") {

    fixedServer = Server.get(fixedServer.id)
    var request = Seq(fixedServer.ips.get(0).id)
    var result  = LoadBalancer.assignToServerIps(testLB.id, request)
    LoadBalancer.waitStatus(testLB.id, GeneralState.ACTIVE)
    assert(result.serverIps.get.size > 0)
  }

  test("List loadBalancers server ips") {

    var result = LoadBalancer.listServerIps(testLB.id)
    assert(result.size > 0)
  }

  test("get loadBalancers server ip") {

    var result = LoadBalancer.getServerIp(testLB.id, fixedServer.ips.get(0).id)
    assert(result.ip == fixedServer.ips.get(0).ip)
  }

  test("unassign loadBalancers server ip") {

    var result = LoadBalancer.unAssignServerIp(testLB.id, fixedServer.ips.get(0).id)
    assert(result.serverIps.get.size == 0)
  }

  test("Add rule") {

    var request = Seq(RuleRequest(LoadBalancerProtocol.TCP, 9000, 9000))
    var result  = LoadBalancer.assignRules(testLB.id, request)
    println(result.rules.get)
    testRule = result.rules.get(1)
    LoadBalancer.waitStatus(testLB.id, GeneralState.ACTIVE)
    assert(result.rules.get.size > 1)
  }

  test("List loadBalancers rules") {

    var result = LoadBalancer.listRules(testLB.id)
    assert(result.size > 0)
  }

  test("get loadBalancers rule") {

    var result = LoadBalancer.getRule(testLB.id, testRule.id)
    assert(result.id == testRule.id)
  }

  test("delete loadBalancers rule") {

    var result = LoadBalancer.deleteRule(testLB.id, testRule.id)
    assert(result.rules.get.size == 1)
  }
}
