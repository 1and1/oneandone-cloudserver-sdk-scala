package oneandone
import oneandone.firewallpolicies.{FirewallPolicy, FirewallPolicyRequest}
import oneandone.loadbalancers.{Loadbalancer, LoadbalancerRequest, RuleRequest}
import oneandone.servers._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class ServerTest extends FunSuite with BeforeAndAfterAll {

  implicit val client                      = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var servers: Seq[Server]                 = Seq.empty
  var fixedInstances: Seq[FixedInstance]   = Seq.empty
  val largeServerInstance: String          = "B77E19E062D5818532EFF11C747BD104"
  var baremetalModels: Seq[BaremetalModel] = Seq.empty
  var customServer: Server                 = null
  var fixedServer: Server                  = null
  var testLB: Loadbalancer                 = null
  var testFP: FirewallPolicy               = null
  var datacenters                          = oneandone.datacenters.Datacenter.list()

  override def beforeAll(): Unit = {
    super.beforeAll()

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

    var fwrequest = FirewallPolicyRequest(
      name = "test scala FP",
      rules = Seq[oneandone.firewallpolicies.RuleRequest](
        oneandone.firewallpolicies.RuleRequest("TCP", "8080")
      )
    )

    testFP = FirewallPolicy.createFirewallPolicy(fwrequest)
    FirewallPolicy.waitFirewallPolicyStatus(testFP.id, GeneralState.ACTIVE)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (testFP != null) {
      FirewallPolicy.waitFirewallPolicyStatus(testFP.id, GeneralState.ACTIVE)
      FirewallPolicy.delete(testFP.id)
    }
    if (testLB != null) {
      Loadbalancer.waitLoadbalancerStatus(testLB.id, GeneralState.ACTIVE)
      Loadbalancer.delete(testLB.id)
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
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    assert(fixedServer.hardware.fixedInstanceSizeId == Some(largeServerInstance))

  }

  test("Create custom hardware server") {

    var request = ServerRequest(
      "custom scala testDD",
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
    Server.waitServerStatus(customServer.id, ServerState.POWERED_ON)
    assert(customServer.hardware.vcore == Some(1.0))
    assert(customServer.hardware.coresPerProcessor == Some(1.0))
    assert(customServer.hardware.ram == Some(2.0))

  }

  test("Modify server information") {
    val updatedName    = "custom server updated nameDD"
    var modifiedServer = Server.modifyServerInformation(customServer.id, updatedName, updatedName)
    Server.waitServerStatus(customServer.id, ServerState.POWERED_ON)
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
    var server = Server.getServerStauts(customServer.id)
    assert(server.state == ServerState.POWERED_ON)
  }

  test("Stop Server") {
    var server = Server.updateStatus(customServer.id, ServerAction.POWER_OFF, "SOFTWARE")
    Server.waitServerStatus(customServer.id, ServerState.POWERED_OFF)
    server = Server.get(customServer.id)
    assert(server.status.state == "POWERED_OFF")
  }

  test("Start Server") {
    var server = Server.updateStatus(customServer.id, ServerAction.POWER_ON, "SOFTWARE")
    Server.waitServerStatus(customServer.id, ServerState.POWERED_ON)
    server = Server.get(customServer.id)
    assert(server.status.state == ServerState.POWERED_ON)
  }

  test("Reboot Server") {
    var server = Server.updateStatus(customServer.id, ServerAction.REBOOT, "SOFTWARE")
    Server.waitServerStatus(customServer.id, ServerState.POWERED_ON)
    server = Server.get(customServer.id)
    assert(server.status.state == ServerState.POWERED_ON)
  }

  test("Load Server DVD") {
    var dvd = Server.loadDvd(fixedServer.id, "57C757F413B340B7056B97C0613B6CCA")
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    assert(dvd != null)
  }

  test("Get Server DVD") {
    var dvd = Server.getServerDvd(fixedServer.id)
    assert(dvd != null)
  }

  test("Unload Server DVD") {
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    var dvd = Server.unloadDvd(fixedServer.id)
    assert(dvd != null)
  }

  test("Get Server Hardware") {
    var hardware = Server.getServerHardware(fixedServer.id)
    assert(hardware != null)
    assert(hardware.fixedInstanceSizeId == fixedServer.hardware.fixedInstanceSizeId)
  }
  var serverHdds: Seq[Hdds] = null
  test("List server hdds") {
    serverHdds = Server.getServerHdds(fixedServer.id)
    assert(serverHdds.size > 0)
  }

  test("Get server hdds") {
    var hddId     = serverHdds(0).id
    var serverHdd = Server.getServersSingleHdd(fixedServer.id, hddId.get)
    assert(serverHdds.size > 0)
  }

  test("Add new server hdds") {
    Server.waitServerStatus(customServer.id, ServerState.POWERED_ON)
    var hdd = HddRequest(60, false)
    var newHdds = Seq[HddRequest](
      hdd
    )

    var serverHdd = Server.addHddToServer(customServer.id, newHdds)
    Server.waitServerStatus(customServer.id, ServerState.POWERED_ON)
    serverHdd = Server.get(customServer.id)
    assert(serverHdd.hardware.hdds.get.size == 2)
  }

  test("Resize server hdds") {
    serverHdds = Server.getServerHdds(customServer.id)
    var hddId     = serverHdds(1).id
    var serverHdd = Server.updateServerSingleHdd(customServer.id, hddId.get, 120)
    Server.waitServerStatus(customServer.id, ServerState.POWERED_ON)
    assert(serverHdd.hardware.hdds.get.size == 2)
  }
  test("Delete server hdds") {
    serverHdds = Server.getServerHdds(customServer.id)
    var hddId     = serverHdds(1).id
    var serverHdd = Server.deleteServerSingleHdd(customServer.id, hddId.get)
    Server.waitServerStatus(customServer.id, ServerState.POWERED_ON)
    assert(serverHdds.size > 0)
  }

  test("Get Server image ") {
    var instance = Server.getServerImage(fixedServer.id)
    assert(instance.id != null)
  }

  test("Reinstall Server Image") {
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    var request = ReinstallImageRequest("753E3C1F859874AA74EB63B3302601F5")
    var image   = Server.reinstallServersImage(fixedServer.id, request)
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    assert(image.id != null)
  }

  test("Stop Server again") {
    var server = Server.updateStatus(customServer.id, ServerAction.POWER_OFF, "HARDWARE")
    Server.waitServerStatus(customServer.id, ServerState.POWERED_OFF)
    server = Server.get(customServer.id)
    assert(server.status.state == ServerState.POWERED_OFF)
  }

  test("Update server hardware") {
    var request = oneandone.servers.UpdateHardwareRequest(
      coresPerProcessor = Some(2),
      vcore = Some(2),
      ram = Some(4))
    var server = Server.updateServerHardware(customServer.id, request)
    Server.waitServerStatus(customServer.id, ServerState.POWERED_OFF)
    server = Server.get(customServer.id)
    assert(server.status.state == "POWERED_OFF")
    assert(server.hardware.coresPerProcessor.get == 2.0)
    assert(server.hardware.ram.get == 4.0)
  }

  test("List fixed instances ") {
    fixedInstances = Server.listFixedInstances()
    assert(fixedInstances.size > 0)
  }

  test("Get fixed instance ") {
    var instance = Server.getFixedInstance(fixedInstances(0).id)
    assert(instance.id == fixedInstances(0).id)
  }

  test("Create server snapshot") {
    var server = Server.createSnapshot(customServer.id)
    Server.waitServerStatus(customServer.id, ServerState.POWERED_OFF)
  }

  var snapshotId = ""
  test("Get server snapshot") {
    var snapshot = Server.getSnapshots(customServer.id)
    snapshotId = snapshot.id
    Server.waitServerStatus(customServer.id, ServerState.POWERED_OFF)
  }

  test("Restore server snapshot") {
    var snapshot = Server.restoreSnapshot(customServer.id, snapshotId)
    Server.waitServerStatus(customServer.id, ServerState.POWERED_OFF)
  }

  test("Delete server snapshot") {
    var snapshot = Server.deleteSnapshot(customServer.id, snapshotId)
    Server.waitServerStatus(customServer.id, ServerState.POWERED_OFF)
  }
  var ips: Seq[Ips] = null
  test("List server IPS") {
    ips = Server.listServerIps(fixedServer.id)
    assert(ips.size > 0)
  }

  test("Assign server IPS") {
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    var server = Server.addNewIPToServer(fixedServer.id, "IPV4")
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    assert(server.ips.size > 0)
  }
  var ip = ""
  test("Get server IPS") {
    var ipsList = Server.listServerIps(fixedServer.id)
    var ipId    = ipsList(0).id
    var ipData  = Server.getServerIp(fixedServer.id, ipId)
    ip = ipData.id
    assert(ip != null)
  }
  var serverClone: Server = null
  test("Clone server") {
    serverClone = Server.cloneServer(fixedServer.id, CloneServerRequest(name = "cloned Server"))
    Server.waitServerStatus(serverClone.id, ServerState.POWERED_ON)
  }

  test("Add firewall policy to IP") {
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    var updatedFW = Server.addFirewallPolicyToServer(fixedServer.id, ip, testFP.id)
    FirewallPolicy.waitFirewallPolicyStatus(testFP.id, GeneralState.ACTIVE)
    assert(updatedFW.ips.get(0).firewallPolicy.get.id == testFP.id)
  }

  test("get firewall policy") {
    var serverIpFwPolicy = Server.getIpsFirewallPolicy(fixedServer.id, ip)
    assert(serverIpFwPolicy.id == testFP.id)
  }

  test("Add loadBalancer to IP") {
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    var updatedLB = Server.addLoadBalancerToServer(fixedServer.id, ip, testLB.id)
    Server.waitServerStatusAndPercentage(fixedServer.id, ServerState.POWERED_ON)
    Loadbalancer.waitLoadbalancerStatus(testLB.id, GeneralState.ACTIVE)
    var updatedServer = Server.get(fixedServer.id)
    assert(updatedServer.ips.get(0).loadBalancers.get(0).id == testLB.id)
  }

  test("get loadBalancer") {
    var serverIpLBPolicy = Server.getIpsLoadBalancers(fixedServer.id, ip)
    assert(serverIpLBPolicy(0).id == testLB.id)
  }

  test("Delete server IPs loadbalancer") {
    Server.waitServerStatusAndPercentage(fixedServer.id, ServerState.POWERED_ON)
    var deleteIp = Server.unassignLoadBalancer(fixedServer.id, ip, testLB.id)
    assert(deleteIp.id != null)
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  test("Delete server IPs") {
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    var deleteIp = Server.deleteServerIp(fixedServer.id, ip)
    assert(deleteIp.id != null)
    Server.waitServerStatusAndPercentage(fixedServer.id, ServerState.POWERED_ON)
  }

  test("Remove Servers") {
    Loadbalancer.waitLoadbalancerStatus(testLB.id, GeneralState.ACTIVE)
    FirewallPolicy.waitFirewallPolicyStatus(testFP.id, GeneralState.ACTIVE)

    Server.waitServerStatus(customServer.id, ServerState.POWERED_OFF)
    Server.delete(customServer.id)
    Server.waitServerStatus(serverClone.id, ServerState.POWERED_ON)
    Server.delete(serverClone.id)
    Thread.sleep(300000)
    Server.waitServerStatusAndPercentage(fixedServer.id, ServerState.POWERED_ON)
    Server.delete(fixedServer.id)

  }

}
