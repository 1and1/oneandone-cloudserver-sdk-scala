package oneandone
import oneandone.privatenetworks.{
  PrivateNetworkRequest,
  PrivateNetwork,
  UpdatePrivateNetworkRequest
}
import oneandone.servers._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class PrivatenetworkTest extends FunSuite with BeforeAndAfterAll {
  implicit val client                      = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var privatenetworks: Seq[PrivateNetwork] = Seq.empty
  var fixedServer: Server                  = null

  var serverIds: List[String]     = List[String]()
  val smallServerInstance: String = "81504C620D98BCEBAA5202D145203B4B"
  var testPn: PrivateNetwork      = null
  var datacenters                 = oneandone.datacenters.Datacenter.list()
  override def beforeAll(): Unit = {
    super.beforeAll()
    for (a <- 1 to 3) {
      var serverRequest = ServerRequest(
        "Scala privatenetowrk  test " + a,
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
      serverIds = serverIds :+ fixedServer.id
    }

  }

  override def afterAll(): Unit = {
    super.afterAll()
    var serverId = ""
    for (id <- serverIds) {
      Server.waitStatus(id, ServerState.POWERED_ON)
      Server.delete(id)
      serverId = id
    }
    Server.waitDeleted(serverId)
    if (testPn != null) {
      PrivateNetwork.waitStatus(testPn.id, GeneralState.ACTIVE)
      PrivateNetwork.delete(testPn.id)
    }
  }

  test("Create Privatenetwork") {

    var request = PrivateNetworkRequest(
      name = "scala test privatenetowrk",
      datacenterId = Some(datacenters(0).id)
    )

    testPn = PrivateNetwork.create(request)
    PrivateNetwork.waitStatus(testPn.id, GeneralState.ACTIVE)

  }

  test("List Privatenetwork") {
    privatenetworks = PrivateNetwork.list()
    assert(privatenetworks.size > 0)
  }

  test("Get Privatenetwork") {
    var bs = PrivateNetwork.get(testPn.id)
    assert(bs.id == testPn.id)
  }

  test("attach server") {

    var bs = PrivateNetwork.attachServer(testPn.id, Seq(fixedServer.id))
    PrivateNetwork.waitStatus(testPn.id, GeneralState.ACTIVE)
    bs = PrivateNetwork.get(testPn.id)
    assert(bs.servers.get(0).id == fixedServer.id)

    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  test("list attached servers") {
    var bs = PrivateNetwork.listServers(testPn.id)
    assert(bs.size > 0)
  }

  test("Get attached server") {
    var bs = PrivateNetwork.getServer(testPn.id, fixedServer.id)
    assert(bs.id == fixedServer.id)
  }

  test("detach server") {
    var bs = PrivateNetwork.detachServer(testPn.id, fixedServer.id)
    assert(bs.servers == None)
    PrivateNetwork.waitStatus(testPn.id, GeneralState.ACTIVE)
  }

  test("Update Privatenetwork") {
    var updateRequest = oneandone.privatenetworks.UpdatePrivateNetworkRequest(
      name = "updated name"
    )
    var bs = PrivateNetwork.update(testPn.id, updateRequest)
    assert(bs.name == "updated name")
    PrivateNetwork.waitStatus(testPn.id, GeneralState.ACTIVE)
  }

  test("attach server from server class") {

    var attachedServer = Server.assignPrivateNetwork(fixedServer.id, testPn.id)
    PrivateNetwork.waitStatus(testPn.id, GeneralState.ACTIVE)
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
    PrivateNetwork.waitStatus(testPn.id, GeneralState.ACTIVE)
    var updated = Server.get(fixedServer.id)
    assert(updated.privateNetworks.get.size > 0)
  }

  test("server list attached private netowrks") {
    var pns = Server.listPrivateNetwork(fixedServer.id)
    assert(pns.size > 0)
  }

  test(" server Get attached private netowrks") {
    var pn = Server.getPrivateNetwork(fixedServer.id, testPn.id)
    assert(pn.id == testPn.id)
  }

  test("Server detach private network") {
    var pns = Server.deletePrivateNetwork(fixedServer.id, testPn.id)
    PrivateNetwork.waitStatus(testPn.id, GeneralState.ACTIVE)
    var updated = PrivateNetwork.get(testPn.id)
    assert(updated.servers == None)
  }

}
