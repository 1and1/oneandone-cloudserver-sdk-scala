package oneandone.roles

case class Permission(
  backups: Option[PermissionDetails] = None,
  blockStorage: Option[PermissionDetails] = None,
  clusters: Option[PermissionDetails] = None,
  firewallPolicies: Option[PermissionDetails] = None,
  images: Option[PermissionDetails] = None,
  interactiveInvoices: Option[PermissionDetails] = None,
  loadBalancers: Option[PermissionDetails] = None,
  logs: Option[PermissionDetails] = None,
  monitoringCenter: Option[PermissionDetails] = None,
  monitoringPolicies: Option[PermissionDetails] = None,
  privateNetworks: Option[PermissionDetails] = None,
  publicIps: Option[PermissionDetails] = None,
  roles: Option[PermissionDetails] = None,
  servers: Option[PermissionDetails] = None,
  sharedStorages: Option[PermissionDetails] = None,
  sshkeys: Option[PermissionDetails] = None,
  usages: Option[PermissionDetails] = None,
  users: Option[PermissionDetails] = None,
  vpn: Option[PermissionDetails] = None
) {}
