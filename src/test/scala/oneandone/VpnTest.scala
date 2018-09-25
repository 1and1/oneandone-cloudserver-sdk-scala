package oneandone
import oneandone.servers.{Hardware, Server, ServerRequest}
import oneandone.vpns.{UpdateVpnRequest, Vpn, VpnRequest}
import org.scalatest.FunSuite

class VpnTest extends FunSuite {
  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var vpn: Seq[Vpn] = Seq.empty
  var testVpn: Vpn = null

  test("List Vpn") {
    vpn = Vpn.list()
    assert(vpn.size > 0)
  }

  test("Create Vpn") {

    var request = VpnRequest(
      name = "vpn scala test"
    )

    testVpn = Vpn.createVpn(request)
    Vpn.waitVpnStatus(testVpn.id, "ACTIVE")

  }

  test("Get Vpn") {
    var vpn = Vpn.get(testVpn.id)
    assert(vpn.id == testVpn.id)
  }

  test("Download Vpn configuration ZIP") {
    Vpn.downloadVpnConfigurationZIP(testVpn.id, "\"C:\\\\temp\"")
  }

  test("Update Vpn") {
    var updateRequest = UpdateVpnRequest(
      name = Some("updated Name"),
    )
    var vpn = Vpn.updateVpn(testVpn.id, updateRequest)
    assert(vpn.name == "updated Name")
    Vpn.waitVpnStatus(testVpn.id, "ACTIVE")
  }

  test("Delete Vpn") {
    var vpn = Vpn.delete(testVpn.id)
    assert(vpn != null)
  }
}
