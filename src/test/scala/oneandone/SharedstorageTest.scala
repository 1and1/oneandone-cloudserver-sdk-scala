package oneandone
import oneandone.servers.{Hardware, Server, ServerRequest}
import oneandone.sharedstorages._
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class SharedstorageTest extends FunSuite with BeforeAndAfter {
  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var Sharedstorages: Seq[Sharedstorage] = Seq.empty
  var fixedServer: Server = null
  val smallServerInstance: String = "81504C620D98BCEBAA5202D145203B4B"
  var testss: Sharedstorage = null
  var datacenters = oneandone.datacenters.Datacenter.list()

  before {
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
    Server.waitServerStatus(fixedServer.id, "POWERED_ON")
  }



  test("Create Sharedstorage") {

    var request = SharedstorageRequest(
      name = "shared storage scala test",
      size = 50,
      datacenterId = Some(datacenters(0).id)
    )

    testss = Sharedstorage.createSharedstorage(request)
    Sharedstorage.waitSharedstorageStatus(testss.id, "ACTIVE")

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
    Sharedstorage.waitSharedstorageStatus(testss.id, "ACTIVE")
  }

  test("attach server") {

    Sharedstorage.waitSharedstorageStatus(testss.id, "ACTIVE")
    var request = AttachServersRequest(
      servers = Seq(
        sharedstorages.ServerRequest(
          id = fixedServer.id,
          rights = "RW"
        )
      )
    )
    var ss = Sharedstorage.attachServer(testss.id, request)
    assert(ss.servers.size > 0)
    Sharedstorage.waitSharedstorageStatus(testss.id, "ACTIVE")
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
    Sharedstorage.waitSharedstorageStatus(testss.id, "ACTIVE")
  }

  test("get access information") {
    var ssA = Sharedstorage.getSharedstorageAccess(testss.id)
    assert(ssA != null)
  }

  test("change password") {
    var ssA = Sharedstorage.updateSharedstoragePassword("test123!")
    assert(ssA != null)
  }

  after {
    if (testss != null) {
      Sharedstorage.waitSharedstorageStatus(testss.id, "ACTIVE")
      Sharedstorage.delete(testss.id)
    }
    if (fixedServer != null) {
      Server.delete(fixedServer.id)
    }
  }

}
