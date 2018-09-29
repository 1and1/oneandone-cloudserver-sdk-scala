package oneandone.monitoringpolicies

case class AddProcessesRequest(
    processes: Seq[ProcessRequest]
) {}
