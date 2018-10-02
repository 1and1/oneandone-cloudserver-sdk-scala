package oneandone.loadbalancers
import oneandone.loadbalancers.HealthCheckTest.HealthCheckTest
import oneandone.loadbalancers.Method.Method

case class LoadbalancerRequest(
    name: String,
    description: Option[String] = None,
    healthCheckTest: HealthCheckTest,
    healthCheckInterval: Double,
    healthCheckPath: Option[String] = None,
    healthCheckParser: Option[String] = None,
    persistence: Boolean,
    persistenceTime: Double,
    method: Method,
    datacenterId: Option[String] = None,
    rules: Seq[RuleRequest]
) {}
