package oneandone
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import oneandone.monitoringcenters.{MonitoringCenter, Period}
import oneandone.monitoringpolicies._
import oneandone.servers.{Hardware, Server, ServerRequest, ServerState}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class MonitoringcenterTest extends FunSuite with BeforeAndAfterAll {
  implicit val client                           = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var fixedServer: Server                       = null
  var createdMonitoringPolicy: MonitoringPolicy = null
  val smallServerInstance: String               = "81504C620D98BCEBAA5202D145203B4B"
  var datacenters                               = oneandone.datacenters.Datacenter.list()
  override def beforeAll(): Unit = {
    super.beforeAll()
    var thresholdDetailWarningA = ThresholdDetail(
      value = 80,
      alert = false
    )

    var thresholdDetailCriticalA = ThresholdDetail(
      value = 90,
      alert = false
    )

    var thresholdDetailWarningB = ThresholdDetail(
      value = 1000,
      alert = true
    )

    var thresholdDetailCriticalB = ThresholdDetail(
      value = 2000,
      alert = true
    )

    var thresholdItemA = ThresholdItem(
      warning = thresholdDetailWarningA,
      critical = thresholdDetailCriticalA
    )

    var thresholdItemB = ThresholdItem(
      warning = thresholdDetailWarningB,
      critical = thresholdDetailCriticalB
    )

    var threshold: Threshold = Threshold(
      cpu = Option(thresholdItemA),
      ram = Option(thresholdItemA),
      internalPing = Option(thresholdItemA),
      transfer = Option(thresholdItemB),
      disk = Option(thresholdItemA)
    )

    var portRequest = PortRequest(
      protocol = "TCP",
      port = 22,
      alertIf = "RESPONDING",
      emailNotification = false
    )

    var processRequest = ProcessRequest(
      process = "Test",
      alertIf = "NOT_RUNNING",
      emailNotification = true
    )

    var createMonitoringPolicyRequest = MonitoringPolicyRequest(
      name = "aaScalaSdkTestMonitoringPolicy",
      description =
        Option("Test - monitoring policy created using oneandone-cloudserver-sdk-scala."),
      email = "test@test.com",
      agent = true,
      thresholds = threshold,
      ports = Seq(portRequest),
      processes = Seq(processRequest)
    )

    createdMonitoringPolicy = MonitoringPolicy.create(createMonitoringPolicyRequest)
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
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)

    MonitoringPolicy.addServers(createdMonitoringPolicy.id, AddServersRequest(Seq(fixedServer.id)))
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (fixedServer != null) {
      Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
      Server.delete(fixedServer.id)
    }
    if (createdMonitoringPolicy != null) {
      MonitoringPolicy.delete(createdMonitoringPolicy.id)
    }
  }
  var mcs: Seq[MonitoringCenter] = Seq.empty

  test("List monitoring policies") {
    mcs = MonitoringCenter.list()
    assert(mcs != null)
  }

  test("Get monitoring policy for server") {
    var today = Calendar.getInstance().getTime()
    //add one day from today

    var dt = new Date()
    val c  = Calendar.getInstance
    c.setTime(dt)
    c.add(Calendar.DATE, 1)
    var tommorow = c.getTime()

    var mc = MonitoringCenter.get(
      fixedServer.id,
      Period.CUSTOM,
      today,
      tommorow
    )
    assert(mc.id == fixedServer.id)
  }

  test("Get monitoring policy for server with fixed period") {

    var mc = MonitoringCenter.get(
      fixedServer.id,
      Period.LAST_HOUR
    )
    println(mc)
    assert(mc.id == fixedServer.id)
  }

}
