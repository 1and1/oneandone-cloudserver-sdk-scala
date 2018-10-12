package oneandone
import oneandone.servers.GeneralState
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

    testVpn = Vpn.create(request)
    Vpn.waitStatus(testVpn.id, GeneralState.ACTIVE)

  }

  test("Get Vpn") {
    var vpn = Vpn.get(testVpn.id)
    assert(vpn.id == testVpn.id)
  }

  test("Download Vpn configuration ZIP") {
    Vpn.downloadVpnConfigurationZIP(testVpn.id, "C:/temp")
  }

  test("Update Vpn") {
    var updateRequest = UpdateVpnRequest(
      name = Some("updated Name"),
    )
    var vpn = Vpn.update(testVpn.id, updateRequest)
    assert(vpn.name == "updated Name")
    Vpn.waitStatus(testVpn.id, GeneralState.ACTIVE)
  }

  test("Delete Vpn") {
    var vpn = Vpn.delete(testVpn.id)
    assert(vpn != null)
  }
}
