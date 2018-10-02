package oneandone
import oneandone.servers._
import oneandone.sharedstorages._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class SharedstorageTest extends FunSuite with BeforeAndAfterAll {
  implicit val client                    = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var Sharedstorages: Seq[Sharedstorage] = Seq.empty
  var fixedServer: Server                = null
  val smallServerInstance: String        = "81504C620D98BCEBAA5202D145203B4B"
  var testss: Sharedstorage              = null
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
    Server.waitServerStatus(fixedServer.id, ServerState.POWERED_ON)
  }

  test("Create Sharedstorage") {

    var request = SharedstorageRequest(
      name = "shared storage scala test",
      size = 50,
      datacenterId = Some(datacenters(0).id)
    )

    testss = Sharedstorage.createSharedstorage(request)
    Sharedstorage.waitSharedstorageStatus(testss.id, GeneralState.ACTIVE)

  }

  test("List Sharedstorage") {
    Sharedstorages = Sharedstorage.list()
    assert(Sharedstorages.size > 0)
  }

  test("Get Sharedstorage") {
    var bs = Sharedstorage.get(testss.id)
    assert(bs.id == testss.id)
  }

  test("Update Sharedstorage") {
    var updateRequest = UpdateSharedstorageRequest(
      name = Some("updated name"),
      size = 100
    )
    var bs = Sharedstorage.updateSharedstorage(testss.id, updateRequest)
    assert(bs.name == "updated name")
    Sharedstorage.waitSharedstorageStatus(testss.id, GeneralState.ACTIVE)
  }

  test("attach server") {

    Sharedstorage.waitSharedstorageStatus(testss.id, GeneralState.ACTIVE)
    var request = AttachServersRequest(
      servers = Seq(
        sharedstorages.SharedStorageServerRequest(
          id = fixedServer.id,
          rights = "RW"
        )
      )
    )
    var ss = Sharedstorage.attachServer(testss.id, request)
    assert(ss.servers.size > 0)
    Sharedstorage.waitSharedstorageStatus(testss.id, GeneralState.ACTIVE)
  }

  test("list attached servers") {
    var ss = Sharedstorage.listAttachedServers(testss.id)
    assert(ss.size > 0)
  }

  test("get attached server") {
    var ss = Sharedstorage.getAttachedServers(testss.id, fixedServer.id)
    assert(ss(0).id == fixedServer.id)
  }

  test("detach server") {
    var bs = Sharedstorage.detachServer(testss.id, fixedServer.id)
    assert(bs.servers.get.size == 0)
    Sharedstorage.waitSharedstorageStatus(testss.id, GeneralState.ACTIVE)
  }

  test("get access information") {
    var ssA = Sharedstorage.getSharedstorageAccess(testss.id)
    assert(ssA != null)
  }

  test("change password") {
    var ssA = Sharedstorage.updateSharedstoragePassword("test123!")
    assert(ssA != null)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (testss != null) {
      Sharedstorage.waitSharedstorageStatus(testss.id, GeneralState.ACTIVE)
      Sharedstorage.delete(testss.id)
    }
    if (fixedServer != null) {
      Server.delete(fixedServer.id)
    }
  }

}
