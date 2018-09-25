package oneandone.loadbalancers

case class UpdateLoadbalancerRequest(
    name: Option[String] = None,
    description: Option[String] = None,
    healthCheckTest: Option[String] = None,
    healthCheckInterval: Option[Double] = None,
    persistence: Option[Boolean] = None,
    persistenceTime: Option[Double] = None,
    method: Option[String] = None
) {}
