package oneandone.images
import oneandone.images.Frequency.Frequency

case class ImageRequest(
    serverId: String,
    name: String,
    frequency: Frequency,
    numImages: Int,
    description: Option[String] = None,
    datacenterId: Option[String] = None,
    source: Option[String] = None,
    url: Option[String] = None,
    osId: Option[String] = None,
    `type`: Option[String] = None,
) {}
