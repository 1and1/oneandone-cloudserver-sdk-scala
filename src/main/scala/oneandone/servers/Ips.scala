package oneandone.servers

case class Ips(
    id: String,
    ip: String,
    `type`: String,
    reverseDns: String,
    firewallPolicy: Option[FirewallPolicy] = None,
    loadBalancers: Option[LoadBalancer] = None,
    main: Boolean
) {}
