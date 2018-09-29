package oneandone.monitoringpolicies

case class MonitoringPolicyRequest(
    name: String,
    description: Option[String],
    email: String,
    agent: Boolean,
    thresholds: Threshold,
    ports: Seq[PortRequest],
    processes: Seq[ProcessRequest]
) {}
