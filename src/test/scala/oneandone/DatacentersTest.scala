package oneandone
import oneandone.datacenters.Datacenter
import org.scalatest.FunSuite

class DatacentersTest extends FunSuite {

  implicit val client              = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var datacenters: Seq[Datacenter] = Seq.empty

  test("List Datacenters") {
    datacenters = Datacenter.list()
    assert(datacenters.size == 4)
  }

  test("Get Datacenter") {
    var dc = Datacenter.get(datacenters(0).id)
    assert(dc.id == datacenters(0).id)
  }

}
