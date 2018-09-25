package oneandone.privatenetworks

case class UpdatePrivateNetworkRequest(
    name: String,
    description: Option[String] = None,
    networkAddress: Option[String] = None,
    subnetMask: Option[String] = None
) {}
