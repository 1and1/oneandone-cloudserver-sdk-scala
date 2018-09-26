package oneandone
import oneandone.usages.Usages
import oneandone.usages.UsageRequestPeriod._
import org.scalatest.FunSuite
import java.util.{Calendar, Date}

class UsagesTest extends FunSuite {

  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var usages: Usages = null

  test("List Usages Response") {
    var dt = new Date()

    val y = Calendar.getInstance
    y.setTime(dt)
    y.add(Calendar.DATE, -20)
    var twentydaysago = y.getTime()
    //add one day from today

    val c = Calendar.getInstance
    c.setTime(dt)
    c.add(Calendar.DATE, 1)
    var tommorow = c.getTime()
    usages = Usages.list(CUSTOM, twentydaysago, tommorow)

    assert(usages != null)
  }

  test("List Usages Response fixed period") {

    usages = Usages.list(LAST_7D)
    assert(usages != null)
  }
}
