package oneandone.images
import oneandone.datacenters.Datacenter
import oneandone.servers.Hdds

case class ImageRequest(
    serverId: String,
    name: String,
    frequency: String,
    numImages: Int,
    description: Option[String] = None,
    datacenterId: Option[String] = None,
    source: Option[String] = None,
    url: Option[String] = None,
    osId: Option[String] = None,
    `type`: Option[String] = None,
) {}
