package oneandone.sharedstorages
import oneandone.datacenters.Datacenter

case class SharedstorageAccess(
    datacenter: Option[Datacenter],
    state: String,
    kerberosContentFile: String,
    needsPasswordReset: Double,
    userDomain: String
) {}
