package oneandone
import oneandone.usages.Usages
import oneandone.usages.UsageRequestPeriod._
import org.scalatest.FunSuite

class UsagesTest extends FunSuite {

  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var usages: Usages = null

  test("List Usages Response") {
    usages = Usages.list(LAST_HOUR)

    assert(usages != null)

    println(usages)
  }
}
