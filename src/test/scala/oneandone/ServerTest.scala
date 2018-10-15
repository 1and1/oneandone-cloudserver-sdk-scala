package oneandone
import oneandone.firewallpolicies.{FirewallPolicy, FirewallPolicyRequest, Protocol}
import oneandone.loadbalancers._
import oneandone.publicips.PublicIp
import oneandone.servers.{LoadBalancer => _, _}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class ServerTest extends FunSuite with BeforeAndAfterAll {

  implicit val client                      = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var servers: Seq[Server]                 = Seq.empty
  var fixedInstances: Seq[FixedInstance]   = Seq.empty
  val largeServerInstance: String          = "B77E19E062D5818532EFF11C747BD104"
  var baremetalModels: Seq[BaremetalModel] = Seq.empty
  var customServer: Server                 = null
  var fixedServer: Server                  = null
  var testLB: LoadBalancer                 = null
  var testFP: FirewallPolicy               = null
  var datacenters                          = oneandone.datacenters.Datacenter.list()

  override def beforeAll(): Unit = {
    super.beforeAll()

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

    var fwrequest = FirewallPolicyRequest(
      name = "test scala FP",
      rules = Seq[oneandone.firewallpolicies.RuleRequest](
        oneandone.firewallpolicies.RuleRequest(Protocol.TCP, "8080")
      )
    )

    testFP = FirewallPolicy.create(fwrequest)
    FirewallPolicy.waitStatus(testFP.id, GeneralState.ACTIVE)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (testFP != null) {
      FirewallPolicy.waitStatus(testFP.id, GeneralState.ACTIVE)
      FirewallPolicy.delete(testFP.id)
    }
    if (testLB != null) {
      LoadBalancer.waitStatus(testLB.id, GeneralState.ACTIVE)
      LoadBalancer.delete(testLB.id)
    }
  }

  test("Create fixed instance server") {

    var request = ServerRequest(
      "fixed sizescala test1",
      Some("desc"),
      Hardware(
        None,
        Some(largeServerInstance),
      ),
      "753E3C1F859874AA74EB63B3302601F5"
    )
    fixedServer = Server.createCloud(request)
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
    assert(fixedServer.hardware.fixedInstanceSizeId == Some(largeServerInstance))

  }

  test("Create custom hardware server") {

    var request = ServerRequest(
      "custom scala test2",
      Some("desc"),
      Hardware(
        None,
        None,
        Some(1.0),
        Some(1.0),
        Some(2.0),
        Some(Seq[Hdds](Hdds(None, 40.0, true)))
      ),
      "753E3C1F859874AA74EB63B3302601F5"
    )
    customServer = Server.createCloud(request)
    Server.waitStatus(customServer.id, ServerState.POWERED_ON)
    assert(customServer.hardware.vcore == Some(1.0))
    assert(customServer.hardware.coresPerProcessor == Some(1.0))
    assert(customServer.hardware.ram == Some(2.0))

  }

  test("Modify server information") {
    val updatedName    = "custom server updated name"
    var modifiedServer = Server.modifyInformation(customServer.id, updatedName, updatedName)
    Server.waitStatus(customServer.id, ServerState.POWERED_ON)
    assert(modifiedServer.name == updatedName)
  }

  test("List Servers") {
    servers = Server.list()
    assert(servers.size > 0)
  }

  test("List Servers With options") {
    val listQueryParams = Map("q" -> "test")
    servers = Server.list(listQueryParams)
    assert(servers.size > 0)
  }

  test("Get Server") {
    var server = Server.get(customServer.id)
    assert(server.id == customServer.id)
  }

  test("Get Server status") {
    var server = Server.getStatus(customServer.id)
    assert(server.state.toString == ServerState.POWERED_ON.toString)
  }

  test("Stop Server") {
    var server =
      Server.updateStatus(customServer.id, ServerAction.POWER_OFF, ActionMethod.SOFTWARE)
    Server.waitStatus(customServer.id, ServerState.POWERED_OFF)
    server = Server.get(customServer.id)
    assert(server.status.state.toString == ServerState.POWERED_OFF.toString)
  }

  test("Start Server") {
    var server = Server.updateStatus(customServer.id, ServerAction.POWER_ON, ActionMethod.SOFTWARE)
    Server.waitStatus(customServer.id, ServerState.POWERED_ON)
    server = Server.get(customServer.id)
    assert(server.status.state.toString == ServerState.POWERED_ON.toString)
  }

  test("Reboot Server") {
    var server = Server.updateStatus(customServer.id, ServerAction.REBOOT, ActionMethod.SOFTWARE)
    Server.waitStatus(customServer.id, ServerState.POWERED_ON)
    server = Server.get(customServer.id)
    assert(server.status.state.toString == (ServerState.POWERED_ON.toString))
  }

  test("Get Server Hardware") {
    var hardware = Server.getHardware(fixedServer.id)
    assert(hardware != null)
    assert(hardware.fixedInstanceSizeId == fixedServer.hardware.fixedInstanceSizeId)
  }
  var serverHdds: Seq[Hdds] = null
  test("List server hdds") {
    serverHdds = Server.getHdds(fixedServer.id)
    assert(serverHdds.size > 0)
  }

  test("Get server hdds") {
    var hddId     = serverHdds(0).id
    var serverHdd = Server.getSingleHdd(fixedServer.id, hddId.get)
    assert(serverHdds.size > 0)
  }

  test("Add new server hdds") {
    Server.waitStatus(customServer.id, ServerState.POWERED_ON)
    var hdd = HddRequest(60, false)
    var newHdds = Seq[HddRequest](
      hdd
    )

    var serverHdd = Server.addHdd(customServer.id, newHdds)
    Server.waitStatusAndPercentage(customServer.id, ServerState.POWERED_ON)
    serverHdd = Server.get(customServer.id)
    assert(serverHdd.hardware.hdds.get.size == 2)
  }

  test("Resize server hdds") {
    serverHdds = Server.getHdds(customServer.id)
    var hddId     = serverHdds(1).id
    var serverHdd = Server.updateSingleHdd(customServer.id, hddId.get, 120)
    Server.waitStatus(customServer.id, ServerState.POWERED_ON)
    assert(serverHdd.hardware.hdds.get.size == 2)
  }

  test("Delete server hdds") {
    serverHdds = Server.getHdds(customServer.id)
    var hddToremove = ""
    for (hdd <- serverHdds) {
      if (!hdd.isMain) {
        hddToremove = hdd.id.get
      }
    }
    var serverHdd = Server.deleteSingleHdd(customServer.id, hddToremove)
    Server.waitStatus(customServer.id, ServerState.POWERED_ON)
    assert(serverHdds.size > 0)
  }

  test("Stop Server again") {
    var server = Server.updateStatus(customServer.id, ServerAction.POWER_OFF, ActionMethod.HARDWARE)
    Server.waitStatus(customServer.id, ServerState.POWERED_OFF)
    server = Server.get(customServer.id)
    assert(server.status.state.toString == ServerState.POWERED_OFF.toString)
  }

  test("Update server hardware") {
    var request = oneandone.servers.UpdateHardwareRequest(
      coresPerProcessor = Some(2),
      vcore = Some(2),
      ram = Some(4))
    var server = Server.updateHardware(customServer.id, request)
    Server.waitStatusAndPercentage(customServer.id, ServerState.POWERED_OFF)
    server = Server.get(customServer.id)
    assert(server.status.state.toString == ServerState.POWERED_OFF.toString)
    assert(server.hardware.coresPerProcessor.get == 2.0)
    assert(server.hardware.ram.get == 4.0)
  }

  var ips: Seq[Ips] = null
  test("List server IPS") {
    ips = Server.listIps(fixedServer.id)
    assert(ips.size > 0)
  }

  test("Assign server IPS") {
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
    var server = Server.addNewIP(fixedServer.id, "IPV4")
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
    assert(server.ips.size > 0)
  }
  var ip = ""
  test("Get server IPS") {
    var ipsList = Server.listIps(fixedServer.id)
    var ipId    = ipsList(0).id
    var ipData  = Server.getIp(fixedServer.id, ipId)
    ip = ipData.id
    assert(ip != null)
  }

  test("Add firewall policy to IP") {
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
    var updatedFW = Server.addFirewallPolicy(fixedServer.id, ip, testFP.id)
    FirewallPolicy.waitStatus(testFP.id, GeneralState.ACTIVE)
    assert(updatedFW.ips.get(0).firewallPolicy.get.id == testFP.id)
  }

  test("get firewall policy") {
    var serverIpFwPolicy = Server.getIpsFirewallPolicy(fixedServer.id, ip)
    assert(serverIpFwPolicy.id == testFP.id)
  }

  test("Add loadBalancer to IP") {
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
    var updatedLB = Server.addLoadBalancer(fixedServer.id, ip, testLB.id)
    Server.waitStatusAndPercentage(fixedServer.id, ServerState.POWERED_ON)
    LoadBalancer.waitStatus(testLB.id, GeneralState.ACTIVE)
    var updatedServer = Server.get(fixedServer.id)
    assert(updatedServer.ips.get(0).loadBalancers.get(0).id == testLB.id)
  }

  test("get loadBalancer") {
    var serverIpLBPolicy = Server.getIpsLoadBalancers(fixedServer.id, ip)
    assert(serverIpLBPolicy(0).id == testLB.id)
  }

  test("Delete server IPs loadbalancer") {
    Server.waitStatusAndPercentage(fixedServer.id, ServerState.POWERED_ON)
    var deleteIp = Server.unassignLoadBalancer(fixedServer.id, ip, testLB.id)
    assert(deleteIp.id != null)
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  test("Delete server IPs") {
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
    var deleteIp = Server.deleteIp(fixedServer.id, ip)
    assert(deleteIp.id != null)
    Server.waitStatusAndPercentage(fixedServer.id, ServerState.POWERED_ON)
    PublicIp.waitStatus(ip, GeneralState.ACTIVE)
  }

  test("Remove Servers") {
    LoadBalancer.waitStatus(testLB.id, GeneralState.ACTIVE)
    FirewallPolicy.waitStatus(testFP.id, GeneralState.ACTIVE)

    Server.waitStatus(customServer.id, ServerState.POWERED_OFF)
    Server.delete(customServer.id)
    Server.waitStatusAndPercentage(fixedServer.id, ServerState.POWERED_ON)
    Server.delete(fixedServer.id)

  }

}
