package oneandone.monitoringcenters

case class AlertsCount(
    resources: Resources,
    ports: Resources,
    process: Resources
) {}
