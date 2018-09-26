package oneandone.monitoringcenters
import oneandone.datacenters.Datacenter

case class ServerUsage(
    id: String,
    name: String,
    status: Status,
    datacenter: Datacenter,
    alertsCount: Option[AlertsCount],
    agent: Option[Agent],
    cpu: Option[ResourceStatus],
    ram: Option[ResourceStatus],
    transfer: Option[ResourceStatus],
    internalPing: Option[ResourceStatus]
) {}
