package oneandone.publicips

case class PublicIpRequest(
    reverseDns: Option[String] = None,
    datacenterId: Option[String] = None,
    `type`: Option[String] = None
) {}
