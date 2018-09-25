package oneandone.vpns

case class VpnRequest(
    name: String,
    description: Option[String] = None,
    datacenterId: Option[String] = None
) {}
