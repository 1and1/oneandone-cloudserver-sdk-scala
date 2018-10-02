package oneandone.loadbalancers
import oneandone.loadbalancers.LoadBalancerProtocol.LoadBalancerProtocol

case class Rule(
    id: String,
    protocol: LoadBalancerProtocol,
    portBalancer: Int,
    portServer: Int,
    source: Option[String] = None
) {}
