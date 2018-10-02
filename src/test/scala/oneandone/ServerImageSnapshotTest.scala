package oneandone
import oneandone.firewallpolicies.{FirewallPolicy, FirewallPolicyRequest, Protocol}
import oneandone.loadbalancers._
import oneandone.servers._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class ServerImageSnapshotTest extends FunSuite with BeforeAndAfterAll {

  implicit val client                      = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var servers: Seq[Server]                 = Seq.empty
  var fixedInstances: Seq[FixedInstance]   = Seq.empty
  val largeServerInstance: String          = "B77E19E062D5818532EFF11C747BD104"
  var baremetalModels: Seq[BaremetalModel] = Seq.empty
  var fixedServer: Server                  = null
  var datacenters                          = oneandone.datacenters.Datacenter.list()

  override def beforeAll(): Unit = {
    super.beforeAll()
    var request = ServerRequest(
      "snapshotImage scala tes1t",
      Some("desc"),
      Hardware(
        None,
        Some(largeServerInstance),
      ),
      "753E3C1F859874AA74EB63B3302601F5"
    )
    fixedServer = Server.createCloud(request)
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)

  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (fixedServer != null) {
      Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
      Server.delete(fixedServer.id)
    }

  }

  var serverClone: Server = null
  test("Clone server") {
    Server.waitServerStatusAndPercentage(fixedServer.id, ServerState.POWERED_ON)
    serverClone = Server.cloneServer(fixedServer.id, CloneServerRequest(name = "cloned Server"))
    Server.waitServerStatus(serverClone.id, ServerState.POWERED_ON)
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

  test("List fixed instances ") {
    fixedInstances = Server.listFixedInstances()
    assert(fixedInstances.size > 0)
  }

  test("Get fixed instance ") {
    var instance = Server.getFixedInstance(fixedInstances(0).id)
    assert(instance.id == fixedInstances(0).id)
  }

  test("Create server snapshot") {
    Server.waitServerStatusAndPercentage(fixedServer.id, ServerState.POWERED_ON)
    var server = Server.createSnapshot(fixedServer.id)
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  var snapshotId = ""
  test("Get server snapshot") {
    var snapshot = Server.getSnapshots(fixedServer.id)
    snapshotId = snapshot.id
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  test("Restore server snapshot") {
    var snapshot = Server.restoreSnapshot(fixedServer.id, snapshotId)
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  test("Delete server snapshot") {
    var snapshot = Server.deleteSnapshot(fixedServer.id, snapshotId)
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  test("Remove Servers") {
    Server.waitServerStatus(serverClone.id, ServerState.POWERED_ON)
    Server.delete(serverClone.id)
  }

}
