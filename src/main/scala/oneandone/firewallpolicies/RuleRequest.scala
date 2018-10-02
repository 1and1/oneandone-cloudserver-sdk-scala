package oneandone.firewallpolicies
import oneandone.firewallpolicies.Protocol.Protocol

case class RuleRequest(
    protocol: Protocol,
    port: String,
    description: Option[String] = None
) {}
