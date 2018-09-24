package oneandone
import oneandone.ping.{Ping, PingAuth}
import org.scalatest.FunSuite

class PingTest extends FunSuite {

  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var ping: Ping = null
  var pingAuth: PingAuth = null

  test("Get Ping Response") {
    ping = Ping.get()

    assert(ping != null)
    assert(ping.value(0) == "PONG")
  }

  test("Get PingAuth Response") {
    pingAuth = PingAuth.get()

    assert(pingAuth != null)
    assert(pingAuth.value(0) == "PONG")
  }
}
