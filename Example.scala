import oneandone.firewallpolicies.{FirewallPolicy, FirewallPolicyRequest, Protocol, RuleRequest}
import oneandone.loadbalancers._
import oneandone.publicips.{IPType, PublicIp, PublicIpRequest}
import oneandone.servers._

class Example {
  implicit val client                = oneandone.OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var datacenters                    = oneandone.datacenters.Datacenter.list()
  var firewallPolicy: FirewallPolicy = null
  var loadBalancer: Loadbalancer     = null
  var exampleIp: PublicIp            = null
  var exampleServer: Server          = null
  var firewallPolicyName             = "example policy"
  var loadBalancerName               = "example Load balancer"
  def main(args: Array[String]): Unit = {
    //create a firewall policy
    var request = FirewallPolicyRequest(
      name = firewallPolicyName,
      rules = Seq[RuleRequest](
        RuleRequest(Protocol.TCP, "8080"),
        RuleRequest(Protocol.TCP, "9000"),
        RuleRequest(Protocol.TCP, "2000"),
      )
    )

    println("creating firewall policy ", firewallPolicyName)
    firewallPolicy = FirewallPolicy.createFirewallPolicy(request)
    FirewallPolicy.waitFirewallPolicyStatus(firewallPolicy.id, GeneralState.ACTIVE)

    //create a load balancer
    var lbRequest = LoadbalancerRequest(
      name = "test scala LB",
      healthCheckTest = HealthCheckTest.TCP,
      healthCheckInterval = 40,
      persistence = true,
      persistenceTime = 1200,
      method = Method.ROUND_ROBIN,
      rules = Seq[loadbalancers.RuleRequest](
        loadbalancers.RuleRequest(LoadBalancerProtocol.TCP, 80, 80)
      ),
      datacenterId = Some(datacenters(0).id)
    )

    println("creating load balancer ", loadBalancerName)
    loadBalancer = Loadbalancer.createLoadbalancer(lbRequest)
    Loadbalancer.waitLoadbalancerStatus(loadBalancer.id, GeneralState.ACTIVE)

    //create and reserve a public ip
    var ipRequest = PublicIpRequest(
      `type` = Some(IPType.IPV4)
    )

    println("creating a public ip")
    exampleIp = PublicIp.createPublicIp(ipRequest)
    PublicIp.waitPublicIpStatus(exampleIp.id, GeneralState.ACTIVE)

    //create a server and assign the previously created resources
    var serverReq = ServerRequest(
      "example scala server",
      Some("desc"),
      Hardware(
        vcore = Some(1.0),
        coresPerProcessor = Some(1.0),
        ram = Some(2.0),
        hdds = Some(Seq[Hdds](Hdds(None, 40.0, true)))
      ),
      firewallPolicyId = Some(firewallPolicy.id),
      applianceId = "753E3C1F859874AA74EB63B3302601F5",
      loadBalancerId = Some(loadBalancer.id),
      ipId = Some(exampleIp.id)
    )
    println("creating cloud example server", "example scala server")
    exampleServer = Server.createCloud(serverReq)
    Server.waitServerStatus(exampleServer.id, ServerState.POWERED_ON)

    println("Server information", Server.get(exampleServer.id))

    //cleanup
    Server.delete(exampleServer.id)
    Server.waitServerDeleted(exampleServer.id)
    FirewallPolicy.delete(firewallPolicy.id)
    Loadbalancer.delete(loadBalancer.id)
    PublicIp.delete(exampleIp.id)

  }

}
