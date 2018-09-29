package oneandone.monitoringpolicies

case class Threshold(
    cpu: Option[ThresholdItem],
    ram: Option[ThresholdItem],
    internalPing: Option[ThresholdItem],
    transfer: Option[ThresholdItem],
    disk: Option[ThresholdItem]
) {}
