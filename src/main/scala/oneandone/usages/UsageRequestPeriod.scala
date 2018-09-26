package oneandone.usages

case class UsageRequestPeriod() {}

object UsageRequestPeriod extends Enumeration {
  type UsageRequestPeriod = Value
  val LAST_HOUR = Value("LAST_HOUR")
  val LAST_24H = Value("LAST_24H")
  val LAST_7D = Value("LAST_7D")
  val LAST_30D = Value("LAST_30D")
  val LAST_365D = Value("LAST_365D")
}