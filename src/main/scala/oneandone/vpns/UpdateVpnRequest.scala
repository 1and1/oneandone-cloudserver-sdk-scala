package oneandone.vpns

case class UpdateVpnRequest(
    name: Option[String] = None,
    description: Option[String] = None,
) {}
