package oneandone.loadbalancers

case class RuleRequest(
    protocol: String,
    portBalancer: Int,
    portServer: Int,
    source: Option[String] = Some("0.0.0.0")
) {}
