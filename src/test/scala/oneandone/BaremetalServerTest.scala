package oneandone
import oneandone.serverappliances.ServerAppliance
import oneandone.servers._
import org.scalatest.FunSuite

class BaremetalServerTest extends FunSuite {

  implicit val client                      = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var servers: Seq[Server]                 = Seq.empty
  var largeBaremetalInstance: String       = ""
  var baremetalModels: Seq[BaremetalModel] = Seq.empty
  var serverApplianceId                    = ""
  var baremetalServer: Server              = null

  test("List baremetal models") {
    baremetalModels = Server.listBaremetalModels()
    for (model <- baremetalModels) {
      if (model.name == "BMC_S") {
        largeBaremetalInstance = model.id
      }

    }
    assert(baremetalModels.size > 0)
  }

  test("Get baremetal models ") {
    var instance = Server.getBaremetalModel(largeBaremetalInstance)
    assert(instance.id == largeBaremetalInstance)
  }

  test("Create baremetal server") {
    var options    = Map("q" -> "baremetal")
    var appliances = ServerAppliance.list(options)
    for (applns <- appliances) {
      if (applns.name.toLowerCase().contains("centos")) {
        serverApplianceId = applns.id
      }
    }
    var request = BaremetalServerRequest(
      "Scala Baremetal test",
      Some("desc"),
      BaremetalHardwareRequest(
        largeBaremetalInstance,
      ),
      applianceId = serverApplianceId
    )
    baremetalServer = Server.createBaremetal(request)
    Server.waitServerStatus(baremetalServer.id, ServerState.POWERED_ON)
    assert(baremetalServer.hardware.baremetalModelId == Some(largeBaremetalInstance))

  }

  test("Modify server information") {
    val updatedName = "custom server updated name"
    var modifiedServer =
      Server.modifyServerInformation(baremetalServer.id, updatedName, updatedName)
    Server.waitServerStatus(baremetalServer.id, ServerState.POWERED_ON)
    assert(modifiedServer.name == updatedName)
  }

  test("Get Server") {
    var server = Server.get(baremetalServer.id)
    assert(server.id == baremetalServer.id)
  }

  test("Get Server status") {
    var server = Server.getServerStauts(baremetalServer.id)
    assert(server.state == ServerState.POWERED_ON)
  }

  test("Stop Server") {
    var server = Server.updateStatus(baremetalServer.id, ServerAction.POWER_OFF, "SOFTWARE")
    Server.waitServerStatus(baremetalServer.id, ServerState.POWERED_OFF)
    server = Server.get(baremetalServer.id)
    assert(server.status.state == ServerState.POWERED_OFF)
  }

  test("Start Server") {
    var server = Server.updateStatus(baremetalServer.id, ServerAction.POWER_ON, "SOFTWARE")
    Server.waitServerStatus(baremetalServer.id, ServerState.POWERED_ON)
    server = Server.get(baremetalServer.id)
    assert(server.status.state == ServerState.POWERED_ON)
  }

  test("Reboot Server") {
    var server = Server.updateStatus(baremetalServer.id, ServerAction.REBOOT, "SOFTWARE")
    Server.waitServerStatus(baremetalServer.id, ServerState.POWERED_ON)
    server = Server.get(baremetalServer.id)
    assert(server.status.state == ServerState.POWERED_ON)
  }

  test("Get Server Hardware") {
    var hardware = Server.getServerHardware(baremetalServer.id)
    assert(hardware != null)
    assert(hardware.baremetalModelId == baremetalServer.hardware.baremetalModelId)
  }

  var ips: Seq[Ips] = null
  test("List server IPS") {
    ips = Server.listServerIps(baremetalServer.id)
    assert(ips.size > 0)
  }

  test("Assign server IPS") {
    Server.waitServerStatus(baremetalServer.id, ServerState.POWERED_ON)
    var server = Server.addNewIPToServer(baremetalServer.id, "IPV4")
    Server.waitServerStatus(baremetalServer.id, ServerState.POWERED_ON)
    assert(server.ips.size > 0)
  }
  var ip = ""
  test("Get server IPS") {
    var ipsList = Server.listServerIps(baremetalServer.id)
    var ipId    = ipsList(0).id
    var ipData  = Server.getServerIp(baremetalServer.id, ipId)
    ip = ipData.id
    assert(ip != null)
  }

  test("Remove Servers") {
    Server.waitServerStatusAndPercentage(baremetalServer.id, ServerState.POWERED_ON)
    Server.delete(baremetalServer.id)
  }

}
