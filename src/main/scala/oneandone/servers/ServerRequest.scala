package oneandone.servers

case class ServerRequest(
    name: String,
    description: Option[String] = None,
    hardware: Hardware,
    applianceId: String,
    datacenterId: Option[String] = None,
    serverType: String = "cloud",
    sshPassword: Option[Boolean] = None,
    publicKey: Option[Seq[String]] = None,
    rsaKey: Option[String] = None,
    password: Option[String] = None,
    powerOn: Boolean = true,
    firewallPolicyId: Option[String] = None,
    ipId: Option[String] = None,
    loadBalancerId: Option[String] = None,
    monitoringPolicyId: Option[String] = None
) {}

case class BaremetalServerRequest(
    name: String,
    description: Option[String] = None,
    hardware: BaremetalHardwareRequest,
    applianceId: String,
    datacenterId: Option[String] = None,
    serverType: String = "baremetal",
    sshPassword: Option[Boolean] = None,
    publicKey: Option[Seq[String]] = None,
    rsaKey: Option[String] = None,
    password: Option[String] = None,
    powerOn: Boolean = true,
    firewallPolicyId: Option[String] = None,
    ipId: Option[String] = None,
    loadBalancerId: Option[String] = None,
    monitoringPolicyId: Option[String] = None
) {}
