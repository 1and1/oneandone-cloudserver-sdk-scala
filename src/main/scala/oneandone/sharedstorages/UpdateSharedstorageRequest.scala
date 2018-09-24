package oneandone.sharedstorages

case class UpdateSharedstorageRequest(
    name: Option[String] = None,
    description: Option[String] = None,
    size: Double
) {}
