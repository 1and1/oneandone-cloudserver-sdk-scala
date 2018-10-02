package oneandone.firewallpolicies
import oneandone.firewallpolicies.Protocol.Protocol

case class Rule(
    id: String,
    protocol: Protocol,
    description: String,
    action: String,
    portFrom: Double,
    portTo: Double,
    source: String
)

object Protocol extends Enumeration {
  type Protocol = Value
  val TCP    = Value("TCP")
  val UDP    = Value("UDP")
  val ICMP   = Value("ICMP")
  val TCPUDP = Value("TCP/UDP")
  val IPSEC  = Value("IPSEC")
  val GRE    = Value("GRE")
  val ANY    = Value("ANY")
}
