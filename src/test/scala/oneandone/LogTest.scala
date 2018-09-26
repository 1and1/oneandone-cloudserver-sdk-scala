package oneandone

import java.util.{Calendar, Date}
import oneandone.RequestPeriod._
import oneandone.logs.Log

import org.scalatest.FunSuite

class LogTest extends FunSuite {
  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var logs: Seq[Log] = Seq.empty
  var log: Log = null

  test("List Logs") {
    var date = new Date()

    val sd = Calendar.getInstance
    sd.setTime(date)
    sd.add(Calendar.MONTH, -1)
    var startDate = sd.getTime()

    val ed = Calendar.getInstance
    ed.setTime(date)
    ed.add(Calendar.DATE, 1)
    var endDate = ed.getTime()
    logs = Log.list(CUSTOM, startDate, endDate)

    assert(logs.size > 0)
  }

  test("Get Log By Id") {
    log = Log.get(logs(0).id)
    assert(log.id == logs(0).id)
  }
}
