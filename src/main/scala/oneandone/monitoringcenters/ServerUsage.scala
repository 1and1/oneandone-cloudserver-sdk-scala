package oneandone.monitoringcenters
import oneandone.datacenters.Datacenter

case class ServerUsage(
    id: String,
    name: String,
    status: Status,
    datacenter: Datacenter,
    alerts: Option[AlertsCount],
    agent: Option[Agent],
    cpu: Option[ResourceStatus],
    ram: Option[ResourceStatus],
    disk: Option[ResourceStatus],
    transfer: Option[ResourceStatus],
    internalPing: Option[ResourceStatus]
) {}
