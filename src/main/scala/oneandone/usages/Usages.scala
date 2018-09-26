package oneandone.usages
import java.text.SimpleDateFormat
import java.util.Date

import oneandone.{BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.native.JsonMethods._

case class Usages(
    servers: Option[List[UsageData]],
    containerCluster: Option[List[UsageData]],
    blockStorage: Option[List[UsageData]],
    images: Option[List[UsageData]],
    storage: Option[List[UsageData]],
    network: Option[List[UsageData]],
    publicIps: Option[List[UsageData]]
) {}

object Usages extends Path {
  override val path: Seq[String] = Seq("usages")

  def list(period: UsageRequestPeriod.Value, startDate: Date = null, endDate: Date = null)(
      implicit client: OneandoneClient
  ): Usages = {
    var fullPath: String = "?period=" + period.toString
    if (period == UsageRequestPeriod.CUSTOM) {
      val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
      var sDate = format.format((startDate))
      var eDate = format.format(endDate)
      fullPath += "&start_date=" + sDate + "&end_date=" + eDate
    }
    var customPath = Seq("usages" + fullPath)
    val response = client.get(customPath)
    val json = parse(snakizeRootKeys(response)).camelizeKeys

    json.extract[Usages]
  }

  def snakizeRootKeys(s: String) =
    s.replaceAll("SERVERS", "servers")
      .replaceAll("CONTAINER CLUSTER", "container_cluster")
      .replaceAll("BLOCK STORAGE", "block_storage")
      .replaceAll("IMAGES", "images")
      .replaceAll("STORAGE", "storage")
      .replaceAll("NETWORK", "network")
      .replaceAll("PUBLIC_IPS", "public_ips")
}
