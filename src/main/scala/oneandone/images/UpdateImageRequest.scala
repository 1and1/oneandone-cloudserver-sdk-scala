package oneandone.images

case class UpdateImageRequest(
    name: String,
    frequency: Option[String] = None,
    description: Option[String] = None  ,
) {}
