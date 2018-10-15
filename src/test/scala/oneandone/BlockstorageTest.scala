package oneandone
import oneandone.blockstorages.{
  BlockStorage,
  BlockstorageRequest,
  StorageState,
  UpdateBlockstorageRequest
}
import oneandone.servers._
import org.scalatest.FunSuite

class BlockstorageTest extends FunSuite {
  implicit val client                  = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var blockStorages: Seq[BlockStorage] = Seq.empty
  var fixedServer: Server              = null
  val smallServerInstance: String      = "81504C620D98BCEBAA5202D145203B4B"
  var testBs: BlockStorage             = null
  var datacenters                      = oneandone.datacenters.Datacenter.list()

  test("Create Blockstorage") {

    var request = BlockstorageRequest(
      name = "scala test bs",
      size = 60,
      datacenterId = datacenters(0).id
    )

    testBs = BlockStorage.create(request)
    BlockStorage.waitStatus(testBs.id, StorageState.POWERED_ON)

  }

  test("List Blockstorage") {
    blockStorages = BlockStorage.list()
    assert(blockStorages.size > 0)
  }

  test("Get Blockstorage") {
    var bs = BlockStorage.get(testBs.id)
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
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)

    var bs = BlockStorage.attachServer(testBs.id, fixedServer.id)
    assert(bs.server.get.id == fixedServer.id)
    BlockStorage.waitStatus(testBs.id, StorageState.POWERED_ON)
  }

  test("Get attached server") {
    var bs = BlockStorage.getServer(testBs.id)
    assert(bs.id == fixedServer.id)
  }

  test("detach server") {
    var bs = BlockStorage.detachServer(testBs.id)
    assert(bs.server == None)
    BlockStorage.waitStatus(testBs.id, StorageState.POWERED_ON)
  }

  test("Update Blockstorage") {
    var updateRequest = UpdateBlockstorageRequest(
      name = Some("updated name")
    )
    var bs = BlockStorage.update(testBs.id, updateRequest)
    assert(bs.name == "updated name")
    BlockStorage.waitStatus(testBs.id, StorageState.POWERED_ON)
  }

  test("Delete Blockstorage") {
    BlockStorage.delete(testBs.id)
    Server.delete(fixedServer.id)
  }
}
