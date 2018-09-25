package oneandone
import oneandone.publicips.{PublicIp, PublicIpRequest}
import oneandone.servers.{Hardware, Server, ServerRequest}
import org.scalatest.FunSuite

class PublicIpTest extends FunSuite {
  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var publicIps: Seq[PublicIp] = Seq.empty
  var testPublicIp: PublicIp = null

  test("List PublicIp") {
    publicIps = PublicIp.list()
    assert(publicIps.size > 0)
  }

  test("Create PublicIp") {

    var request = PublicIpRequest(
      `type` = Some("IPV4")
    )

    testPublicIp = PublicIp.createPublicIp(request)
    PublicIp.waitPublicIpStatus(testPublicIp.id, "ACTIVE")

  }

  test("Get PublicIp") {
    var publicIp = PublicIp.get(testPublicIp.id)
    assert(publicIp.id == testPublicIp.id)
  }

  test("Update PublicIp") {

    var publicIp = PublicIp.updatePublicIp(testPublicIp.id, "test.com")
    assert(publicIp.reverseDns.get == "test.com")
    PublicIp.waitPublicIpStatus(testPublicIp.id, "ACTIVE")
  }

  test("Delete PublicIp") {
    var publicIp = PublicIp.delete(testPublicIp.id)
    assert(publicIp != null)
  }
}