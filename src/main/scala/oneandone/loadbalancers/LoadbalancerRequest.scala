package oneandone.loadbalancers

case class LoadbalancerRequest(
    name: String,
    description: Option[String] = None,
    healthCheckTest: String,
    healthCheckInterval: Double,
    healthCheckPath: Option[String] = None,
    healthCheckParser: Option[String] = None,
    persistence: Boolean,
    persistenceTime: Double,
    method: String,
    datacenterId: Option[String] = None,
    rules: Seq[RuleRequest]
) {}
