package oneandone
import oneandone.servers.{FixedInstance, Server}
import org.scalatest.FunSuite

class ServerTest extends FunSuite {

  implicit val client                    = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var servers: Seq[Server]               = Seq.empty
  var fixedInstances: Seq[FixedInstance] = Seq.empty

  test("List Servers") {
    servers = Server.list()
    assert(servers.size == 10)
  }

  test("Get Server") {
    var server = Server.get(servers(0).id)
    assert(server.id == servers(0).id)
  }

  test("List fixed instances ") {
    fixedInstances = Server.listFixedInstances()
    assert(fixedInstances.size > 0)
  }

  test("Get fixed instance ") {
    var instance = Server.getFixedInstances(fixedInstances(0).id)
    assert(instance.id == fixedInstances(0).id)
  }

}
