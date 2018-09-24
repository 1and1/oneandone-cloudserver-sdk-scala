package oneandone.sharedstorages

case class SharedstorageRequest(
    size: Double,
    name: String,
    description: Option[String] = None,
    datacenterId: Option[String] = None,
) {}
