package oneandone.usages

case class UsageService(
  name: String,
  `type`: String,
  avgAmount: Integer,
  unit: String,
  usage: Integer,
  detail: List[UsageDetail]
) {}
