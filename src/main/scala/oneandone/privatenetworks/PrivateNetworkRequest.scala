package oneandone.privatenetworks

case class PrivateNetworkRequest(
    name: String,
    description: Option[String] = None,
    datacenterId: Option[String] = None,
    networkAddress: Option[String] = None,
    subnetMask: Option[String] = None
) {}
