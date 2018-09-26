package oneandone.monitoringcenters

case class StatusResource(
    cpu: ResourceStatus,
    transfer: ResourceStatus,
    internalPing: ResourceStatus,
    ram: ResourceStatus,
    disk: ResourceStatus
) {}
