package oneandone.monitoringpolicies

case class PortRequest(
    protocol: String,
    port: Integer,
    alertIf: String,
    emailNotification: Boolean
) {}
