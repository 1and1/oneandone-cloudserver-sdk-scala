package oneandone
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import oneandone.monitoringcenters.{Monitoringcenter, Period}
import oneandone.servers.{Hardware, Server, ServerRequest}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class MonitoringcenterTest extends FunSuite with BeforeAndAfterAll {
  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var fixedServer: Server = null
  val smallServerInstance: String = "81504C620D98BCEBAA5202D145203B4B"
  var datacenters = oneandone.datacenters.Datacenter.list()
  override def beforeAll(): Unit = {
    super.beforeAll()

    var serverRequest = ServerRequest(
      "Scala monitoring policy 2test",
      Some("desc"),
      Hardware(
        None,
        Some(smallServerInstance),
      ),
      "753E3C1F859874AA74EB63B3302601F5",
      Some(datacenters(0).id)
    )
    fixedServer = Server.createCloud(serverRequest)
    Server.waitServerStatus(fixedServer.id, "POWERED_ON")
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (fixedServer != null) {
      Server.delete(fixedServer.id)
    }
  }
  var mcs: Seq[Monitoringcenter] = Seq.empty

  test("List monitoring policies") {
    mcs = Monitoringcenter.list()
    assert(mcs != null)
  }

  test("Get monitoring policy for server") {
    var today = Calendar.getInstance().getTime()
    //add one day from today

    var dt = new Date()
    val c = Calendar.getInstance
    c.setTime(dt)
    c.add(Calendar.DATE, 1)
    var tommorow = c.getTime()

    //todo: add monitoring policy on server and fix the overall enums

    var mc = Monitoringcenter.get(
      fixedServer.id,
      Period.CUSTOM,
      today,
      tommorow
    )
    assert(mc.id == fixedServer.id)
  }

  test("Get monitoring policy for server with fixed period") {

    var mc = Monitoringcenter.get(
      fixedServer.id,
      Period.LAST_HOUR
    )
    println(mc)
    assert(mc.id == fixedServer.id)
  }

}
