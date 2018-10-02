package oneandone.loadbalancers
import oneandone.loadbalancers.LoadBalancerProtocol.LoadBalancerProtocol

case class RuleRequest(
    protocol: LoadBalancerProtocol,
    portBalancer: Int,
    portServer: Int,
    source: Option[String] = Some("0.0.0.0")
) {}

object LoadBalancerProtocol extends Enumeration {
  type LoadBalancerProtocol = Value
  val TCP = Value("TCP")
  val UDP = Value("UDP")
}
