package oneandone

import oneandone.serverappliances.ServerAppliance
import org.scalatest.FunSuite

class ServerAppliancesTest extends FunSuite {

  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var serverAppliances: Seq[ServerAppliance] = Seq.empty

  test("List Server Appliances") {
    serverAppliances = ServerAppliance.list()
    assert(serverAppliances.size > 0)
  }

  test("Get a Server Appliance") {
    var serverAppliance = ServerAppliance.get(serverAppliances(0).id)
    assert(serverAppliance.id == serverAppliances(0).id)
  }
}