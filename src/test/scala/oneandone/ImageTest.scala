package oneandone
import oneandone.images.{Frequency, Image, ImageRequest, UpdateImageRequest}
import oneandone.servers.{Hardware, Server, ServerRequest, ServerState}
import org.scalatest.FunSuite

class ImageTest extends FunSuite {
  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var images: Seq[Image] = Seq.empty
  var fixedServer: Server = null
  val smallServerInstance: String = "81504C620D98BCEBAA5202D145203B4B"
  var testImage: Image = null

  test("List Image") {
    images = Image.list()
    assert(images.size > 0)
  }

  test("Create Image") {

    var serverRequest = ServerRequest(
      "Scala Image Test",
      Some("desc"),
      Hardware(
        None,
        Some(smallServerInstance),
      ),
      "753E3C1F859874AA74EB63B3302601F5"
    )
    fixedServer = Server.createCloud(serverRequest)
    Server.waitStatus(fixedServer.id, ServerState.POWERED_ON)
    var request = ImageRequest(
      serverId = fixedServer.id,
      name = "scala test image",
      frequency = Frequency.ONCE,
      numImages = 1
    )

    testImage = Image.create(request)
    Image.waitStatus(testImage.id, "ENABLED")

  }

  test("Get Image") {
    var image = Image.get(testImage.id)
    assert(image.id == testImage.id)
  }

  test("Update Image") {
    var updateRequest = UpdateImageRequest(
      name = "updated Name",
      frequency = Some(Frequency.ONCE)
    )
    var image = Image.update(testImage.id, updateRequest)
    assert(image.name == "updated Name")
    Image.waitStatus(testImage.id, "ENABLED")
  }

  test("Delete Image") {
    var image = Image.delete(testImage.id)
    assert(image != null)

    var server = Server.delete(fixedServer.id)
  }
}
