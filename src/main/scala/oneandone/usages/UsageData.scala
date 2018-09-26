package oneandone.usages

case class UsageData(
  id: String,
  name: String,
  services: List[UsageService]
) {}
