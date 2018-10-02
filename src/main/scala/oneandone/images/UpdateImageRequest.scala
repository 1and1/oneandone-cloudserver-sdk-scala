package oneandone.images
import oneandone.images.Frequency.Frequency

case class UpdateImageRequest(
    name: String,
    frequency: Option[Frequency] = None,
    description: Option[String] = None  ,
) {}
