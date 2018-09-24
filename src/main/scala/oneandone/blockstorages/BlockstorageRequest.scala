package oneandone.blockstorages

case class BlockstorageRequest(
    name: String,
    description: Option[String] = None,
    size: Double,
    datacenterId: String,
    serverId: Option[String] = None
) {}
