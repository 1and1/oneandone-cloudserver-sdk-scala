package oneandone.prices

case class PricingPlans(
  servers: Option[ServerPrice] = None,
  publicIps: Option[List[PublicIpPrice]] = None,
  image: Option[ImagePrice] = None,
  sharedStorage: Option[SharedStoragePrice] = None,
  softwareLicences: Option[List[SoftwareLicencePrice]] = None,
  backups: Option[BackupPrice] = None
) {}