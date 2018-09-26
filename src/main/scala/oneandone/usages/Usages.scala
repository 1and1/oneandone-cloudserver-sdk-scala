package oneandone.usages
import oneandone.{BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
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

  def list(period: UsageRequestPeriod.Value)(implicit client: OneandoneClient): Usages = {
    val updatedPath: Seq[String] = Seq(path(0) + "?period=" + period.toString())
    val response = client.get(updatedPath)
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
