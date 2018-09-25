package oneandone.loadbalancers

case class Rule(
    id: String,
    protocol: String,
    portBalancer: Int,
    portServer: Int,
    source: Option[String] = None
) {}
