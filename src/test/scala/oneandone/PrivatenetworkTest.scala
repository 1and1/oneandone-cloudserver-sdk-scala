package oneandone
import oneandone.privatenetworks.{
  PrivateNetworkRequest,
  Privatenetwork,
  UpdatePrivateNetworkRequest
}
import oneandone.servers._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class PrivatenetworkTest extends FunSuite with BeforeAndAfterAll {
  implicit val client                      = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var privatenetworks: Seq[Privatenetwork] = Seq.empty
  var fixedServer: Server                  = null

  var serverIds: List[String]     = List[String]()
  val smallServerInstance: String = "81504C620D98BCEBAA5202D145203B4B"
  var testPn: Privatenetwork      = null
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
      Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
      serverIds = serverIds :+ fixedServer.id
    }

  }

  override def afterAll(): Unit = {
    super.afterAll()
    var serverId = ""
    for (id <- serverIds) {
      Server.waitServerStatus(id, ServerState.POWERED_ON)
      Server.delete(id)
      serverId = id
    }
    Server.waitServerDeleted(serverId)
    if (testPn != null) {
      Privatenetwork.waitPrivatenetworkStatus(testPn.id, GeneralState.ACTIVE)
      Privatenetwork.delete(testPn.id)
    }
  }

  test("Create Privatenetwork") {

    var request = PrivateNetworkRequest(
      name = "scala test privatenetowrk",
      datacenterId = Some(datacenters(0).id)
    )

    testPn = Privatenetwork.createPrivatenetwork(request)
    Privatenetwork.waitPrivatenetworkStatus(testPn.id, GeneralState.ACTIVE)

  }

  test("List Privatenetwork") {
    privatenetworks = Privatenetwork.list()
    assert(privatenetworks.size > 0)
  }

  test("Get Privatenetwork") {
    var bs = Privatenetwork.get(testPn.id)
    assert(bs.id == testPn.id)
  }

  test("attach server") {

    var bs = Privatenetwork.attachServer(testPn.id, Seq(fixedServer.id))
    Privatenetwork.waitPrivatenetworkStatus(testPn.id, GeneralState.ACTIVE)
    bs = Privatenetwork.get(testPn.id)
    assert(bs.servers.get(0).id == fixedServer.id)

    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  test("list attached servers") {
    var bs = Privatenetwork.listServers(testPn.id)
    assert(bs.size > 0)
  }

  test("Get attached server") {
    var bs = Privatenetwork.getServer(testPn.id, fixedServer.id)
    assert(bs.id == fixedServer.id)
  }

  test("detach server") {
    var bs = Privatenetwork.detachServer(testPn.id, fixedServer.id)
    assert(bs.servers == None)
    Privatenetwork.waitPrivatenetworkStatus(testPn.id, GeneralState.ACTIVE)
  }

  test("Update Privatenetwork") {
    var updateRequest = oneandone.privatenetworks.UpdatePrivateNetworkRequest(
      name = "updated name"
    )
    var bs = Privatenetwork.updatePrivatenetwork(testPn.id, updateRequest)
    assert(bs.name == "updated name")
    Privatenetwork.waitPrivatenetworkStatus(testPn.id, GeneralState.ACTIVE)
  }

  test("attach server from server class") {

    var attachedServer = Server.assignPrivateNetwork(fixedServer.id, testPn.id)
    Privatenetwork.waitPrivatenetworkStatus(testPn.id, GeneralState.ACTIVE)
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
    Privatenetwork.waitPrivatenetworkStatus(testPn.id, GeneralState.ACTIVE)
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
    Privatenetwork.waitPrivatenetworkStatus(testPn.id, GeneralState.ACTIVE)
    var updated = Privatenetwork.get(testPn.id)
    assert(updated.servers == None)
  }

}
