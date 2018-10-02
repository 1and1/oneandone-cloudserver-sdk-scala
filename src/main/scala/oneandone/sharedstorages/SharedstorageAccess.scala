package oneandone.sharedstorages
import oneandone.datacenters.Datacenter
import oneandone.servers.GeneralState.GeneralState

case class SharedstorageAccess(
    datacenter: Option[Datacenter],
    state: GeneralState,
    kerberosContentFile: String,
    needsPasswordReset: Double,
    userDomain: String
) {}
