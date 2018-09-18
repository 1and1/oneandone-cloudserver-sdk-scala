package oneandone
import oneandone.servers._
import org.scalatest.FunSuite

class ServerTest extends FunSuite {

  implicit val client                      = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var servers: Seq[Server]                 = Seq.empty
  var fixedInstances: Seq[FixedInstance]   = Seq.empty
  var largeServerInstance: FixedInstance   = null
  var baremetalModels: Seq[BaremetalModel] = Seq.empty
  var customServer: Server                 = null
  var fixedServer: Server                  = null

  test("List fixed instances with options ") {
    val listQueryParams = Map("q" -> "B77E19E062D5818532EFF11C747BD104")
    var result          = Server.listFixedInstances(listQueryParams)
    largeServerInstance = result(0)
    assert(largeServerInstance != null)

  }

  test("Create fixed instance server") {

    var request = ServerRequest(
      "fixed sizescala test13",
      Some("desc"),
      Hardware(
        None,
        Some(largeServerInstance.id),
      ),
      "753E3C1F859874AA74EB63B3302601F5"
    )
    fixedServer = Server.createCloud(request)
    Server.waitServerStatus(fixedServer.id, "POWERED_ON")
    assert(fixedServer.hardware.fixedInstanceSizeId == Some(largeServerInstance.id))

  }

  test("Create custom hardware server") {

    var request = ServerRequest(
      "custom scala test13",
      Some("desc"),
      Hardware(
        None,
        None,
        Some(2.0),
        Some(2.0),
        Some(2.0),
        Some(Seq[Hdds](Hdds(None, 40.0, true)))
      ),
      "753E3C1F859874AA74EB63B3302601F5"
    )
    customServer = Server.createCloud(request)
    Server.waitServerStatus(customServer.id, "POWERED_ON")
    assert(customServer.hardware.vcore == Some(2.0))
    assert(customServer.hardware.coresPerProcessor == Some(2.0))
    assert(customServer.hardware.ram == Some(2.0))

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
    var server = Server.get(servers(0).id)
    assert(server.id == servers(0).id)
  }

  test("List fixed instances ") {
    fixedInstances = Server.listFixedInstances()
    assert(fixedInstances.size > 0)
  }

  test("Get fixed instance ") {
    var instance = Server.getFixedInstance(fixedInstances(0).id)
    assert(instance.id == fixedInstances(0).id)
  }

  test("List baremetal models") {
    baremetalModels = Server.listBaremetalModels()
    assert(baremetalModels.size > 0)
  }

  test("Get baremetal models ") {
    var instance = Server.getBaremetalModel(baremetalModels(0).id)
    assert(instance.id == baremetalModels(0).id)
  }

  test("Remove Servers") {
    Server.delete(customServer.id)
    Server.delete(fixedServer.id)

  }

}
