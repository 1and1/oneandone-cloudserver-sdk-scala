package oneandone.firewallpolicies

case class Rule(
    id: String,
    protocol: String,
    description: String,
    action: String,
    portFrom: Double,
    portTo: Double,
    source: String
)
