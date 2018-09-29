package oneandone.monitoringpolicies

case class ThresholdItem(
    warning: ThresholdDetail,
    critical: ThresholdDetail
) {}
