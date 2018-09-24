package oneandone
import oneandone.blockstorages.{Blockstorage, BlockstorageRequest, UpdateBlockstorageRequest}
import oneandone.servers.{Datacenter, Hardware, Server, ServerRequest}
import org.scalatest.FunSuite

class BlockstorageTest extends FunSuite {
  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var blockStorages: Seq[Blockstorage] = Seq.empty
  var fixedServer: Server = null
  val smallServerInstance: String = "81504C620D98BCEBAA5202D145203B4B"
  var testBs: Blockstorage = null
  var datacenters = oneandone.datacenters.Datacenter.list()

  test("Create Blockstorage") {

    var request = BlockstorageRequest(
      name = "scala test bs",
      size = 60,
      datacenterId = datacenters(0).id
    )

    testBs = Blockstorage.createBlockstorage(request)
    Blockstorage.waitBlockstorageStatus(testBs.id, "POWERED_ON")

  }

  test("List Blockstorage") {
    blockStorages = Blockstorage.list()
    assert(blockStorages.size > 0)
  }

  test("Get Blockstorage") {
    var bs = Blockstorage.get(testBs.id)
    assert(bs.id == testBs.id)
  }

  test("attach server") {

    var serverRequest = ServerRequest(
      "Scala block storage test",
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

    var bs = Blockstorage.attachServer(testBs.id, fixedServer.id)
    assert(bs.server.get.id == fixedServer.id)
    Blockstorage.waitBlockstorageStatus(testBs.id, "POWERED_ON")
  }

  test("Get attached server") {
    var bs = Blockstorage.getServer(testBs.id)
    assert(bs.id == fixedServer.id)
  }

  test("detach server") {
    var bs = Blockstorage.detachServer(testBs.id)
    assert(bs.server == None)
    Blockstorage.waitBlockstorageStatus(testBs.id, "POWERED_ON")
  }

  test("Update Blockstorage") {
    var updateRequest = UpdateBlockstorageRequest(
      name = Some("updated name")
    )
    var bs = Blockstorage.updateBlockstorage(testBs.id, updateRequest)
    assert(bs.name == "updated name")
    Blockstorage.waitBlockstorageStatus(testBs.id, "POWERED_ON")
  }

  test("Delete Blockstorage") {
    Blockstorage.delete(testBs.id)
    Server.delete(fixedServer.id)
  }
}
