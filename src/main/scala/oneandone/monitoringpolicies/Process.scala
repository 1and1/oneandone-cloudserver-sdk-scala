package oneandone.monitoringpolicies

case class Process(
    id: String,
    process: String,
    alertIf: String,
    emailNotification: Boolean
) {}
