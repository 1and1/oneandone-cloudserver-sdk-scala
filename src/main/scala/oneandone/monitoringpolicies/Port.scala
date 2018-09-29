package oneandone.monitoringpolicies

case class Port(
    id: String,
    protocol: String,
    port: Integer,
    alertIf: String,
    emailNotification: Boolean
) {}
