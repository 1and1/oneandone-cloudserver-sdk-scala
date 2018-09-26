package oneandone.monitoringcenters
import oneandone.datacenters.Datacenter
import oneandone.monitoringcenters.Period.Period
import oneandone.{BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods.parse

case class Monitoringcenter(
    id: String,
    name: String,
    status: Status,
    alerts: Alerts,
    agent: Agent,
    datacenter: Datacenter
) {}

object Period extends Enumeration {
  type Period = Value
  val LAST_HOUR, LAST_24H, LAST_7D, LAST_30D, LAST_365D, CUSTOM = Value
}

object Monitoringcenter extends Path {
  val serializers = List(BooleanCustomSerializer)
  override implicit lazy val serializerFormats: Formats = DefaultFormats ++ serializers
  override val path: Seq[String] = Seq("monitoring_center")

  def list()(implicit client: OneandoneClient): Seq[Monitoringcenter] = {
    val response = client.get(path)
    val json = parse(response).camelizeKeys
    json.extract[Seq[Monitoringcenter]]
  }

  def get(id: String, period: Period, startDate: String = "", endDate: String = "")(
      implicit client: OneandoneClient
  ): ServerUsage = {
    var fullPath: String = id + "?period=" + period.toString
    if (period == Period.CUSTOM) {
      fullPath += "&start_date=" + startDate + "&end_date=" + endDate
    }
    val response = client.get(path :+ fullPath)
    val json = parse(response).camelizeKeys
    json.extract[ServerUsage]
  }

}
