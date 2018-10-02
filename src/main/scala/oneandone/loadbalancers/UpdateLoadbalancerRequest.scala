package oneandone.loadbalancers
import oneandone.loadbalancers.HealthCheckTest.HealthCheckTest
import oneandone.loadbalancers.Method.Method

case class UpdateLoadbalancerRequest(
    name: Option[String] = None,
    description: Option[String] = None,
    healthCheckTest: Option[HealthCheckTest] = None,
    healthCheckInterval: Option[Double] = None,
    persistence: Option[Boolean] = None,
    persistenceTime: Option[Double] = None,
    method: Option[Method] = None
) {}
