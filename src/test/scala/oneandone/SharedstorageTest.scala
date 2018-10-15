package oneandone
import oneandone.servers._
import oneandone.sharedstorages._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class SharedstorageTest extends FunSuite with BeforeAndAfterAll {
  implicit val client                    = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var Sharedstorages: Seq[SharedStorage] = Seq.empty
  var fixedServer: Server                = null
  val smallServerInstance: String        = "81504C620D98BCEBAA5202D145203B4B"
  var testss: SharedStorage              = null
  var datacenters                        = oneandone.datacenters.Datacenter.list()

  override def beforeAll(): Unit = {
    super.beforeAll()
    var serverRequest = ServerRequest(
      "Scala shared storage test2",
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
  }

  test("Create Sharedstorage") {

    var request = SharedstorageRequest(
      name = "shared storage scala test",
      size = 50,
      datacenterId = Some(datacenters(0).id)
    )

    testss = SharedStorage.create(request)
    SharedStorage.waitStatus(testss.id, GeneralState.ACTIVE)

  }

  test("List Sharedstorage") {
    Sharedstorages = SharedStorage.list()
    assert(Sharedstorages.size > 0)
  }

  test("Get Sharedstorage") {
    var bs = SharedStorage.get(testss.id)
    assert(bs.id == testss.id)
  }

  test("Update Sharedstorage") {
    var updateRequest = UpdateSharedstorageRequest(
      name = Some("updated name"),
      size = 100
    )
    var bs = SharedStorage.update(testss.id, updateRequest)
    assert(bs.name == "updated name")
    SharedStorage.waitStatus(testss.id, GeneralState.ACTIVE)
  }

  test("attach server") {

    SharedStorage.waitStatus(testss.id, GeneralState.ACTIVE)
    var request = AttachServersRequest(
      servers = Seq(
        sharedstorages.SharedStorageServerRequest(
          id = fixedServer.id,
          rights = "RW"
        )
      )
    )
    var ss = SharedStorage.attachServer(testss.id, request)
    assert(ss.servers.size > 0)
    SharedStorage.waitStatus(testss.id, GeneralState.ACTIVE)
  }

  test("list attached servers") {
    var ss = SharedStorage.listAttachedServers(testss.id)
    assert(ss.size > 0)
  }

  test("get attached server") {
    var ss = SharedStorage.getAttachedServers(testss.id, fixedServer.id)
    assert(ss(0).id == fixedServer.id)
  }

  test("detach server") {
    var bs = SharedStorage.detachServer(testss.id, fixedServer.id)
    assert(bs.servers.get.size == 0)
    SharedStorage.waitStatus(testss.id, GeneralState.ACTIVE)
  }

  test("get access information") {
    var ssA = SharedStorage.getAccessSettings(testss.id)
    assert(ssA != null)
  }

  test("change password") {
    var ssA = SharedStorage.updatePassword("test123!")
    assert(ssA != null)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (testss != null) {
      SharedStorage.waitStatus(testss.id, GeneralState.ACTIVE)
      SharedStorage.delete(testss.id)
    }
    if (fixedServer != null) {
      Server.delete(fixedServer.id)
    }
  }

}
