package oneandone

import oneandone.monitoringpolicies._
import oneandone.servers._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class MonitoringPolicyTest extends FunSuite with BeforeAndAfterAll {
  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  val smallServerInstance: String = "81504C620D98BCEBAA5202D145203B4B"
  var monitoringPolicies: Seq[MonitoringPolicy] = Seq.empty
  var monitoringPolicy, createdMonitoringPolicy, updatedMonitoringPolicy: MonitoringPolicy = null
  var monitoringPolicyPorts: Seq[Port] = Seq.empty
  var monitoringPolicyPort: Port = null
  var monitoringPolicyProcesses: Seq[Process] = Seq.empty
  var monitoringPolicyProcess: Process = null
  var monitoringPolicyServers: Seq[BasicResource] = Seq.empty
  var monitoringPolicyServer: BasicResource = null
  var fixedServer: Server = null
  var datacenters = oneandone.datacenters.Datacenter.list()

  override def beforeAll(): Unit = {
    super.beforeAll()
    var serverRequest = ServerRequest(
      "Scala monitoring policy test1",
      Some("A very descriptive description"),
      Hardware(
        None,
        Some(smallServerInstance),
      ),
      "753E3C1F859874AA74EB63B3302601F5",
      Some(datacenters(0).id)
    )
    fixedServer = Server.createCloud(serverRequest)
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (fixedServer != null) {
      Server.delete(fixedServer.id)
      Server.waitServerDeleted(fixedServer.id)
    }
  }

  test("Create Monitoring Policy") {
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
      name = "aaScalaSdkTestMonitoringPolicy1",
      description =
        Option("Test - monitoring policy created using oneandone-cloudserver-sdk-scala."),
      email = "test@test.com",
      agent = true,
      thresholds = threshold,
      ports = Seq(portRequest),
      processes = Seq(processRequest)
    )

    createdMonitoringPolicy = MonitoringPolicy.createMonitoringPolicy(createMonitoringPolicyRequest)

    assert(createdMonitoringPolicy != null)
    assert(
      true == MonitoringPolicy.waitMonitoringPolicyStatus(createdMonitoringPolicy.id, GeneralState.ACTIVE)
    )
  }

  test("List Monitoring Policies") {
    monitoringPolicies = MonitoringPolicy.list()

    assert(monitoringPolicies.size > 0)
  }

  test("Get Monitoring Policy") {
    monitoringPolicy = MonitoringPolicy.get(createdMonitoringPolicy.id)

    assert(monitoringPolicy.id == createdMonitoringPolicy.id)
  }

  test("Update Monitoring Policy") {
    var thresholdDetailWarningA = ThresholdDetail(
      value = 85,
      alert = true
    )

    var thresholdDetailCriticalA = ThresholdDetail(
      value = 95,
      alert = true
    )

    var thresholdDetailWarningB = ThresholdDetail(
      value = 1500,
      alert = false
    )

    var thresholdDetailCriticalB = ThresholdDetail(
      value = 1900,
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
      alertIf = "NOT_RESPONDING",
      emailNotification = true
    )

    var processRequest = ProcessRequest(
      process = "Test",
      alertIf = "NOT_RUNNING",
      emailNotification = false
    )

    var updateMonitoringPolicyRequest = MonitoringPolicyRequest(
      name = "aaScalaSdkTestMonitoringPolicyUpdated",
      description =
        Option("Test - monitoring policy updated using oneandone-cloudserver-sdk-scala."),
      email = "test@testing.com",
      agent = true,
      thresholds = threshold,
      ports = Seq(portRequest),
      processes = Seq(processRequest)
    )

    updatedMonitoringPolicy = MonitoringPolicy.updateMonitoringPolicy(
      createdMonitoringPolicy.id,
      updateMonitoringPolicyRequest
    )

    assert(updatedMonitoringPolicy != null)
    assert(
      true == MonitoringPolicy.waitMonitoringPolicyStatus(updatedMonitoringPolicy.id, GeneralState.ACTIVE)
    )
    assert(createdMonitoringPolicy.id == updatedMonitoringPolicy.id)
    assert(updatedMonitoringPolicy.name == "aaScalaSdkTestMonitoringPolicyUpdated")
  }

  test("Add Port To Monitoring Policy") {
    var portRequest = PortRequest(
      protocol = "UDP",
      port = 80,
      alertIf = "NOT_RESPONDING",
      emailNotification = false
    )

    var response =
      MonitoringPolicy.addPorts(createdMonitoringPolicy.id, AddPortsRequest(Seq(portRequest)))

    assert(response != null)
    assert(
      true == MonitoringPolicy.waitMonitoringPolicyStatus(createdMonitoringPolicy.id, GeneralState.ACTIVE)
    )
  }

  test("List Monitoring Policy Ports") {
    monitoringPolicyPorts = MonitoringPolicy.listPorts(createdMonitoringPolicy.id)

    assert(monitoringPolicyPorts.length == 2)
    assert(monitoringPolicyPorts(0).protocol == "TCP")
    assert(monitoringPolicyPorts(1).protocol == "UDP")
  }

  test("Get Monitoring Policy Port") {
    monitoringPolicyPort =
      MonitoringPolicy.getPort(createdMonitoringPolicy.id, monitoringPolicyPorts(1).id)

    assert(monitoringPolicyPort != null)
    assert(monitoringPolicyPort.protocol == "UDP")
    assert(monitoringPolicyPort.alertIf == "NOT_RESPONDING")
  }

  test("Update Monitoring Policy Port") {
    var portRequest = PortRequest(
      protocol = "TCP",
      port = 433,
      alertIf = "RESPONDING",
      emailNotification = true
    )

    var updatePortRequest = UpdatePortRequest(portRequest)

    monitoringPolicy = MonitoringPolicy.updatePort(
      createdMonitoringPolicy.id,
      monitoringPolicyPort.id,
      updatePortRequest
    )

    assert(
      true == MonitoringPolicy.waitMonitoringPolicyStatus(createdMonitoringPolicy.id, GeneralState.ACTIVE)
    )
    assert(monitoringPolicy.ports.get(1).alertIf == "RESPONDING")
    assert(monitoringPolicy.ports.get(1).emailNotification == true)
  }

  test("Delete Monitoring Policy Port") {
    var result = MonitoringPolicy.deletePort(createdMonitoringPolicy.id, monitoringPolicyPort.id)
    assert(result != null)
    MonitoringPolicy.waitMonitoringPolicyStatus(createdMonitoringPolicy.id, GeneralState.ACTIVE)

    monitoringPolicyPorts = MonitoringPolicy.listPorts(createdMonitoringPolicy.id)
    assert(monitoringPolicyPorts.length == 1)
  }

  test("Add Process To Monitoring Policy") {
    var processRequest = ProcessRequest(
      process = "TestAddProccess",
      alertIf = "RUNNING",
      emailNotification = false
    )

    var response =
      MonitoringPolicy.addProccesses(
        createdMonitoringPolicy.id,
        AddProcessesRequest(Seq(processRequest))
      )

    assert(response != null)
    assert(
      true == MonitoringPolicy.waitMonitoringPolicyStatus(createdMonitoringPolicy.id, GeneralState.ACTIVE)
    )
  }

  test("List Monitoring Policy Processes") {
    monitoringPolicyProcesses = MonitoringPolicy.listProcesses(createdMonitoringPolicy.id)

    assert(monitoringPolicyProcesses.length == 2)
    assert(monitoringPolicyProcesses(0).process == "Test")
    assert(monitoringPolicyProcesses(0).alertIf == "NOT_RUNNING")
    assert(monitoringPolicyProcesses(0).emailNotification == true)
  }

  test("Get Monitoring Policy Process") {
    monitoringPolicyProcess =
      MonitoringPolicy.getProcess(createdMonitoringPolicy.id, monitoringPolicyProcesses(1).id)

    assert(monitoringPolicyProcess.process == "TestAddProccess")
    assert(monitoringPolicyProcess.alertIf == "RUNNING")
    assert(monitoringPolicyProcess.emailNotification == false)
  }

  test("Update Monitoring Policy Process") {
    var processRequest = ProcessRequest(
      process = "TestAddProccessUpdated",
      alertIf = "NOT_RUNNING",
      emailNotification = true
    )

    var updateProcessRequest = UpdateProcessRequest(processRequest)

    monitoringPolicy = MonitoringPolicy.updateProcess(
      createdMonitoringPolicy.id,
      monitoringPolicyProcess.id,
      updateProcessRequest
    )

    assert(
      true == MonitoringPolicy.waitMonitoringPolicyStatus(createdMonitoringPolicy.id, GeneralState.ACTIVE)
    )

    assert(monitoringPolicy.processes.get(1).alertIf == "NOT_RUNNING")
    assert(monitoringPolicy.processes.get(1).emailNotification == true)
  }

  test("Delete Monitoring Policy Process") {
    var result =
      MonitoringPolicy.deleteProcess(createdMonitoringPolicy.id, monitoringPolicyProcess.id)
    assert(result != null)
    MonitoringPolicy.waitMonitoringPolicyStatus(createdMonitoringPolicy.id, GeneralState.ACTIVE)

    monitoringPolicyProcesses = MonitoringPolicy.listProcesses(createdMonitoringPolicy.id)
    assert(monitoringPolicyProcesses.length == 1)
  }

  test("Add Server To Monitoring Policy") {
    var result = MonitoringPolicy.addServers(
      createdMonitoringPolicy.id,
      AddServersRequest(Seq(fixedServer.id))
    )

    assert(
      true == MonitoringPolicy.waitMonitoringPolicyStatus(createdMonitoringPolicy.id, GeneralState.ACTIVE)
    )
    assert(result.servers != null)
    assert(result.servers.length > 0)
  }

  test("List Monitoring Policy Servers") {
    monitoringPolicyServers = MonitoringPolicy.listServers(createdMonitoringPolicy.id)

    assert(monitoringPolicyServers.length == 1)
    assert(monitoringPolicyServers(0).id == fixedServer.id)
  }

  test("Get Monitoring Policy Server") {
    monitoringPolicyServer = MonitoringPolicy.getServer(createdMonitoringPolicy.id, fixedServer.id)

    assert(monitoringPolicyServer.id == fixedServer.id)
    assert(monitoringPolicyServer.name == "Scala monitoring policy test1")
  }

  test("Delete Monitoring Policy Server") {
    var result =
      MonitoringPolicy.deleteServer(createdMonitoringPolicy.id, monitoringPolicyServer.id)

    assert(result != null)
    assert(
      true == MonitoringPolicy.waitMonitoringPolicyStatus(createdMonitoringPolicy.id, GeneralState.ACTIVE)
    )
  }

  test("Delete Monitoring Policy") {
    var deletedMonitoringPolicy = MonitoringPolicy.delete(createdMonitoringPolicy.id)

    assert(deletedMonitoringPolicy != null)
    assert(deletedMonitoringPolicy == Seq.empty)
  }
}
