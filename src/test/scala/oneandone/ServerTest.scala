package oneandone
import oneandone.servers.Server
import org.scalatest.FunSuite

class ServerTest extends FunSuite {

  implicit val client              = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var servers: Seq[Server] = Seq.empty

  test("List Servers") {
    servers = Server.list()
    assert(servers.size == 10)
  }

  test("Get Server") {
    var server = Server.get(servers(0).id)
    assert(server.id == servers(0).id)
  }

  test("Get Server") {
    var server = Server.get(servers(0).id)
    assert(server.id == servers(0).id)
  }

}
