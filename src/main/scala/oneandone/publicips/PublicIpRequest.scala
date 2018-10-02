package oneandone.publicips
import oneandone.publicips.IPType.IPType

case class PublicIpRequest(
    reverseDns: Option[String] = None,
    datacenterId: Option[String] = None,
    `type`: Option[IPType] = None
) {}
