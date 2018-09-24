package oneandone.firewallpolicies

case class FirewallPolicyRequest(
    name: String,
    description: Option[String] = None,
    rules: Seq[RuleRequest]
) {}
