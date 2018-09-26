package oneandone.monitoringcenters

case class Status(
    state: String,
    cpu: ResourceStatus,
    transfer: ResourceStatus,
    internalPing: ResourceStatus,
    ram: ResourceStatus,
    disk: ResourceStatus
) {}
