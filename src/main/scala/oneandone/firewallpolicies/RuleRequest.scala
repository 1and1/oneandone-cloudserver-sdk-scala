package oneandone.firewallpolicies

case class RuleRequest(
    protocol: String,
    port: String,
    description: Option[String] = None
) {}
