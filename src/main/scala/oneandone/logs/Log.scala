package oneandone.logs

import java.text.SimpleDateFormat
import java.util.Date
import org.json4s.native.JsonMethods._
import oneandone.{OneandoneClient, Path, RequestPeriod}
import oneandone.servers.Status

case class Log(
    id: String,
    startDate: String,
    endDate: String,
    duration: Integer,
    status: Status,
    action: String,
    `type`: String,
    resource: LogResource,
    user: LogResource
) {}

object Log extends oneandone.Path {
  override val path: Seq[String] = Seq("logs")

  def list(period: RequestPeriod.Value, startDate: Date = null, endDate: Date = null)(
    implicit client: OneandoneClient
  ): Seq[Log] = {
    var fullPath: String = "?period=" + period.toString

    if (period == RequestPeriod.CUSTOM) {
      val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
      var sDate = format.format((startDate))
      var eDate = format.format(endDate)
      fullPath += "&start_date=" + sDate + "&end_date=" + eDate
    }
    var customPath = Seq("logs" + fullPath)
    val response = client.get(customPath)
    val json = parse(response).camelizeKeys

    json.extract[Seq[Log]]
  }

  def get(id: String)(implicit client: OneandoneClient): Log = {
    val response = client.get(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[Log]
  }
}
