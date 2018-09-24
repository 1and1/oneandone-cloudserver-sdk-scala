package oneandone.blockstorages

case class UpdateBlockstorageRequest(
    name: Option[String] = None,
    description: Option[String] = None,
    size: Option[Double] = None,
) {}
