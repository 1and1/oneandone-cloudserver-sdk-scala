package oneandone.monitoringpolicies

case class ProcessRequest(
    process: String,
    alertIf: String,
    emailNotification: Boolean
) {}
