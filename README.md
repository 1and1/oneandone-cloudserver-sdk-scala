# 1&1 Cloudserver Scala SDK

The 1&1 Scala SDK is a Scala library designed for interaction with the 1&1 cloud platform over the REST API.

This guide contains instructions on getting started with the library and automating various management tasks available through the 1&1 Cloud Panel UI. For more information on the 1&1 Cloudserver Scala SDK see the [1&1 Community Portal](https://www.1and1.com/cloud-community/).

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
  * [Installation](#installation)
  * [Authentication](#authentication)
- [Operations](#operations)
  - [Servers](#servers)
  - [Images](#images)
  - [Shared Storages](#shared-storages)
  - [Firewall Policies](#firewall-policies)
  - [Load Balancers](#load-balancers)
  - [Public IPs](#public-ips)
  - [Private Networks](#private-networks)
  - [VPNs](#vpns)  
  - [Monitoring Center](#monitoring-center)
  - [Monitoring Policies](#monitoring-policies)
  - [Logs](#logs)
  - [Users](#users)
  - [Roles](#roles)
  - [Usages](#usages)
  - [Server Appliances](#server-appliances)
  - [DVD ISO](#dvd-iso)
  - [Ping](#ping)
  - [Pricing](#pricing)
  - [Data Centers](#data-centers)
  - [Block Storages](#block-storages)
  - [SSH Keys](#ssh-keys)
- [Example](#example)


## Overview

This SDK is a wrapper for the 1&1 REST API written in Scala. All operations against the API are performed over SSL and authenticated using your 1&1 token key. The Scala library facilitates the access to the REST API within an instance running on 1&1 platform.

For more information on the 1&1 Cloud Server SDK for Scala, visit the [Community Portal](https://www.1and1.com/cloud-community/).

## Getting Started

Before you begin you will need to have signed up for a 1&1 account. The credentials you create during sign-up will be used to authenticate against the API.

You will also need to install the Scala language tools. Find the install package and instructions on the  official [Scala-lang website](https://www.scala-lang.org/download/). Make sure to follow the setup instructions.

### Installation

The official Scala library is available from the 1&1 GitHub account found [here](https://github.com/1and1/oneandone-cloudserver-sdk-Scala).

### Authentication

Set the authentication token as an Environment Variable `ONEANDONE_TOKEN` and create the API client:

```
implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
```


Refer to the [Example](#example) and [Operations](#operations) sections for additional information.


## Operations
### Servers

**List all servers:**

`Server.list()`

To paginate the list of servers received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of servers that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of servers sorted in expected order pass a server property (e.g. `"name"`) in Map("sort" -> "-name") parameter.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the server instances that contain it.

To retrieve a collection of servers containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,name,description,hardware.ram"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve a single server:**

`Server.get(serverid)`

**List fixed-size server templates:**

`Server.listFixedInstances()`

**Retrieve information about a fixed-size server template:**

`Server.getFixedInstance(instanceId)`

**Retrieve information about a server's hardware:**

`Server.getHardware(serverId)`

**List a server's HDDs:**

`Server.getHdds(serverId)`

**Retrieve a single server HDD:**

`Server.getSingleHdd(serverId, hddId.get)`

**Retrieve information about a server's image:**

`Server.getImage(serverId)`

**List a server's IPs:**

`Server.listIps(serverId)`

**Retrieve information about a single server IP:**

`Server.getIp(serverId)`

**List all firewall policies assigned to a server IP:**

`Server.getIpsFirewallPolicy(serverId, ip)`

**List all load balancers assigned to a server IP:**

`Server.getIpsLoadBalancers(serverId, ip)`

**Retrieve information about a server's status:**

`Server.getStatus(serverId)`

**Retrieve information about the DVD loaded into the virtual DVD unit of a server:**

`Server.getDvd(serverId)`

**List a server's private networks:**

`Server.listPrivateNetwork(serverId)`

**Retrieve information about a server's private network:**

`Server.getPrivateNetwork(serverId, privateNetworkId)`

**List all server's snapshots:**

`Server.getSnapshots(serverId)`

**Create a server:**

```
var request = ServerRequest(
      name = "custom scala test2",
      description = Some("desc"),
      Hardware(
        vcore = Some(1.0),
        coresPerProcessor = Some(1.0),
        ram = Some(2.0),
        hdds = Some(Seq[Hdds](Hdds(None, 40.0, true)))
      ),
      applianceId =  "753E3C1F859874AA74EB63B3302601F5"
    )
    customServer = Server.createCloud(request)
    Server.waitStatus(customServerId, ServerState.POWERED_ON)
```

**Create a fixed-size server**

```
 var request = ServerRequest(
      name ="fixed sizescala test1",
      description= Some("desc"),
      Hardware(
        fixedInstanceSizeId=Some(largeServerInstance),
      ),
      applianceId = "753E3C1F859874AA74EB63B3302601F5"
    )
    fixedServer = Server.createCloud(request)
    Server.waitStatus(serverId, ServerState.POWERED_ON)
```

**Create a baremetal server**

```
 var request = BaremetalServerRequest(
      "Scala Baremetal test",
      Some("desc"),
      BaremetalHardwareRequest(
        baremetalModelId,
      ),
      applianceId = serverApplianceId
    )
    baremetalServer = Server.createBaremetal(request)
    Server.waitStatus(baremetalServer.id, ServerState.POWERED_ON)
```

**Update a server:**

`Server.modifyInformation(serverId, updatedName, updatedDescription)`

**Delete a server:**

`Server.delete(serverId)`

**Update a server's hardware:**

```
var request = oneandone.servers.UpdateHardwareRequest(
      coresPerProcessor = Some(2),
      vcore = Some(2),
      ram = Some(4))
var server = Server.updateHardware(serverId, request)
```

**Add new hard disk(s) to a server:**

```
var hdd = HddRequest(60, false)
var newHdds = Seq[HddRequest](
     hdd
)

var serverHdd = Server.addHdd(serverId, newHdds)
```

**Resize a server's hard disk:**

`var serverHdd = Server.updateSingleHdd(serverId, hddId, newSizeInt)`

**Remove a server's hard disk:**

`Server.deleteSingleHdd(serverId, hddToremove)`

**Load a DVD into the virtual DVD unit of a server:**

`Server.loadDvd(serverId, dvdId)`

**Unload a DVD from the virtual DVD unit of a server:**

`Server.unloadDvd(serverId)`

**Reinstall a new image into a server:**
```
var request = ReinstallImageRequest(imageId)
var image   = Server.reinstallServersImage(serverId, request)
```

**Assign a new IP to a server:**

`Server.addNewIP(serverId, "IPV4")`

**Release an IP and remove it from a server:**

`Server.deleteIp(serverId, ip)`


**Assign a new firewall policy to a server's IP:**

`Server.addFirewallPolicy(serverId, ip, firewallPolicyId)`

**Assign a new load balancer to a server's IP:**

`Server.addLoadBalancer(serverId, ip, loadBalancerId)`

**Remove a load balancer from a server's IP:**

`Server.unassignLoadBalancer(serverId, ip, loadBalancerId)`

**Start a server:**

`Server.updateStatus(serverId, ServerAction.POWER_ON, ActionMethod.SOFTWARE)`

**Reboot a server:**

`Server.updateStatus(serverId, ServerAction.REBOOT, ActionMethod.SOFTWARE)`

Set `ActionMethod` to either for `SOFTWARE` or `HARDWARE`for method of rebooting.

**Shutdown a server:**

`Server.updateStatus(serverId, ServerAction.POWER_OFF, ActionMethod.SOFTWARE)`

**Assign a private network to a server:**

`Server.assignPrivateNetwork(serverId, privateNetworkId)`

**Remove a server's private network:**

`Server.deletePrivateNetwork(serverId, privateNetworkId)`

**Create a new server's snapshot:**

`Server.createSnapshot(serverId)`

**Restore a server's snapshot:**

`Server.restoreSnapshot(serverId, snapshotId)`

**Remove a server's snapshot:**

`Server.deleteSnapshot(serverId, snapshotId)`

**Clone a server:**

`Server.clone(serverId, CloneServerRequest(name = "cloned Server"))`


### Images

**List all images:**

`Image.list()`

To paginate the list of images received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. set `Map("per_page" -> "1")` to the number of images that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of images sorted in expected order, pass an image property (e.g. `"name"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` to search for a string in the response and return only the elements that contain it.

To retrieve a collection of images containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,name,creation_date"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve a single image:**

`Image.get(testImageId)`


**Create an image:**

```
var request = ImageRequest(
      serverId = serverId,
      name = "scala test image",
      frequency = Frequency.ONCE,
      numImages = 1
    )
Image.create(request)
```

All fields except `Description` are required. `Frequency` may be set to `"ONCE"`, `"DAILY"` or `"WEEKLY"`.

**Update an image:**

```
var updateRequest = UpdateImageRequest(
      name = "updated Name",
	  frequency = Some(Frequency.ONCE)
    )
Image.update(testImageId, updateRequest)
```

`Frequency` may be set to `"ONCE"`, `"DAILY"` or `"WEEKLY"`.

**Delete an image:**

`Image.delete(testImageId)`

### Shared Storages

`Sharedstorage.list()`

To paginate the list of shared storages received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of volumes that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of shared storages sorted in expected order, pass a volume property (e.g. `"name"`) in `Map("sort" -> "name")` parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the volume instances that contain it.

To retrieve a collection of shared storages containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,name,size,size_used"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve a shared storage:**

`Sharedstorage.get(sharedStorageId)`


**Create a shared storage:**

```
var request = SharedstorageRequest(
     name = "shared storage scala test",
     size = 50,
     datacenterId = Some(data-centers)
   )

Sharedstorage.create(request)
```

`Description` is optional parameter.


**Update a shared storage:**

```
var updateRequest = UpdateSharedstorageRequest(
      name = Some("updated name"),
      size = 100
    )
Sharedstorage.update(sharedStorageId, updateRequest)
```
All request's parameters are optional.


**Remove a shared storage:**

`Sharedstorage.delete(sharedStorageId)`


**List a shared storage servers:**

`Sharedstorage.listAttachedServers(sharedStorageId)`


**Retrieve a shared storage server:**

`Sharedstorage.getAttachedServers(sharedStorageId, serverId)`


**Add servers to a shared storage:**

```
var request = AttachServersRequest(
      servers = Seq(
        sharedstorages.SharedStorageServerRequest(
          id = serverId,
          rights = "RW"
        )
      )
    )
Sharedstorage.attachServer(sharedStorageId, request)
```
`rights` may be set to `R` or `RW` string.

				
**Remove a server from a shared storage:**

`Sharedstorage.detachServer(sharedStorageId, serverId)`


**Retrieve the credentials for accessing the shared storages:**

`Sharedstorage.getAccessSettings(sharedStorageId)`


**Change the password for accessing the shared storages:**

`Sharedstorage.updatePassword(complexPassword)`


### Firewall Policies

**List firewall policies:**

`FirewallPolicy.list()`

To paginate the list of firewall policies received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of firewall policies that will be shown in each page.  `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of firewall policies sorted in expected order, pass a firewall policy property (e.g. `"name"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the firewall policy instances that contain it.

To retrieve a collection of firewall policies containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,name,creation_date"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve a single firewall policy:**

`FirewallPolicy.get(policyId)`


**Create a firewall policy:**

```
var request = FirewallPolicyRequest(
      name = "test scala FP",
      rules = Seq[RuleRequest](
        RuleRequest(Protocol.TCP, "8080")
      )
    )

FirewallPolicy.create(request)
```
`setSource` and `setDescription` are optional parameters.

			
**Update a firewall policy:**

```
var updateRequest = UpdateFirewallPolicyRequest(
      name = "new name scala test"
    )
FirewallPolicy.update(policyId, updateRequest)
```
			
**Delete a firewall policy:**

`FirewallPolicy.delete(policyId)`


**List servers/IPs attached to a firewall policy:**

`FirewallPolicy.listServerIps(policyId)`


**Retrieve information about a server/IP assigned to a firewall policy:**

`FirewallPolicy.getServerIp(policyId, ip)`


**Add servers/IPs to a firewall policy:**

```
var request = Seq(ipId)
FirewallPolicy.assignToServerIps(policyId, request)
```

**List rules of a firewall policy:**

`FirewallPolicy.listRules(policyId)`


**Retrieve information about a rule of a firewall policy:**

`FirewallPolicy.getRule(policyId, ruleid)`


**Adds new rules to a firewall policy:**

```
var request = Seq(RuleRequest(Protocol.TCP, "9000"))
FirewallPolicy.assignRules(policyId, request)
```

**Remove a rule from a firewall policy:**

`FirewallPolicy.deleteRule(policyId, ruleId)`


### Load Balancers

**List load balancers:**

`Loadbalancer.list()`

To paginate the list of load balancers received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of load balancers that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of load balancers sorted in expected order, pass a load balancer property (e.g. `"name"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the load balancer instances that contain it.

To retrieve a collection of load balancers containing only the requested fields, pass a list of comma-separated properties (e.g. `"ip,name,method"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve a single load balancer:**

`Loadbalancer.get(loadBalancerId)`


**Create a load balancer:**

```
var request = LoadbalancerRequest(
      name = "test scala LB",
      healthCheckTest = HealthCheckTest.TCP,
      healthCheckInterval = 40,
      persistence = true,
      persistenceTime = 1200,
      method = Method.ROUND_ROBIN,
      rules = Seq[RuleRequest](
        RuleRequest(LoadBalancerProtocol.TCP, 80, 80)
      ),
      datacenterId = Some(datacenterId)
    )

Loadbalancer.create(request)

```
Optional parameters are `HealthCheckPath`, `HealthCheckPathParser`, `Source` and `Description`. Load balancer `Method` must be set to `"ROUND_ROBIN"` or `"LEAST_CONNECTIONS"`.

**Update a load balancer:**

```
 var updateRequest = UpdateLoadbalancerRequest(
      name = Some("new name scala test")
    )
Loadbalancer.updateLoadbalancer(testLBId, updateRequest)
```
All updatable fields are optional.


**Delete a load balancer:**

`Loadbalancer.delete(loadBalancerId)`


**List servers/IPs attached to a load balancer:**

`Loadbalancer.listServerIps(loadBalancerId)`


**Retrieve information about a server/IP assigned to a load balancer:**

`Loadbalancer.getServerIp(loadBalancerId, ipId)`


**Add servers/IPs to a load balancer:**

```
var request = Seq(ipId)
Loadbalancer.assignToServerIps(loadBalancerId, request)
```


**Remove a server/IP from a load balancer:**

`Loadbalancer.unAssignServerIps(loadBalancerId, ipId)`


**List rules of a load balancer:**

`Loadbalancer.listRules(loadBalancerId)`


**Retrieve information about a rule of a load balancer:**

`Loadbalancer.getRule(loadBalancerId, ruleId)`


**Adds new rules to a load balancer:**

```
var request = Seq(RuleRequest(LoadBalancerProtocol.TCP, 9000, 9000))
Loadbalancer.assignRules(loadBalancerId, request)
```

**Remove a rule from a load balancer:**

`Loadbalancer.deleteRule(loadBalancerId, ruleId)`


### Public IPs

**Retrieve a list of your public IPs:**

`PublicIp.list()`

To paginate the list of public IPs received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of public IPs that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of public IPs sorted in expected order, pass a public IP property (e.g. `"ip"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the public IP instances that contain it.

To retrieve a collection of public IPs containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,ip,reverse_dns"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.


**Retrieve a single public IP:**

`PublicIp.get(publicIp)`


**Create a public IP:**

```
var request = PublicIpRequest(
      `type` = Some(IPType.IPV4)
    )

PublicIp.create(request)
```

Both parameters are optional and may be left blank. `ip_type` may be set to `"IPType.IPV4"` or `"IPType.IPV6"`. Presently, only IPV4 is supported.

**Update the reverse DNS of a public IP:**

```
PublicIp.update(testPublicIpId, "test.com")
```

If an empty string is passed in `reverseDns,` it removes previous reverse dns of the public IP.

**Remove a public IP:**

`PublicIp.delete(testPublicIpId)`


### Private Networks

**List all private networks:**

`Privatenetwork.list()`

To paginate the list of private networks received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of private networks that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of private networks sorted in expected order pass a private network property (e.g. `"-creation_date"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the private network instances that contain it.

To retrieve a collection of private networks containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,name,creation_date"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters Map("sort" -> "-name"), `Map("q" -> "test")` or Map("fields" -> "id,name") is blank, it is ignored in the request.

**Retrieve information about a private network:**

`Privatenetwork.get(privateNetworkId)`

**Create a new private network:**

```
var request = PrivateNetworkRequest(
      name = "scala test privatenetowrk",
      datacenterId = Some(dataCenterId)
    )

Privatenetwork.create(request)
```
Private network `Name` is required parameter.


**Modify a private network:**

```
var updateRequest = oneandone.privatenetworks.UpdatePrivateNetworkRequest(
      name = "updated name"
    )
Privatenetwork.update(privateNetworkId, updateRequest)
```
All parameters in the request are optional.


**Delete a private network:**

`Privatenetwork.delete(privateNetworkId)`


**List all servers attached to a private network:**

`Privatenetwork.listServers(privateNetworkId)`


**Retrieve a server attached to a private network:**

`Privatenetwork.getServer(privateNetworkId, serverId)`


**Attach servers to a private network:**

` Privatenetwork.attachServer(privateNetworkId, Seq(serverId))`
*Note:* Servers cannot be attached to a private network if they currently have a snapshot.


**Remove a server from a private network:**

`Privatenetwork.detachServer(privateNetworkId, serverId)`

*Note:* The server cannot be removed from a private network if it currently has a snapshot or it is powered on.

### VPNs

**List all VPNs:**

`Vpn.list()`

To paginate the list of VPNs received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set ` per_page` to the number of VPNs that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less or equal to zero, the parameters are ignored by the framework.

To receive the list of VPNs sorted in expected order pass a VPN property (e.g. `"name"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the VPN instances that contain it.

To retrieve a collection of VPNs containing only the requested fields pass a list of comma separated properties (e.g. `"id,name,creation_date"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve information about a VPN:**

`Vpn.get(testVpnId)`

**Create a VPN:**

```
 var request = VpnRequest(
      name = "vpn scala test"
    )

Vpn.create(request)
```

**Modify a VPN:**

```
var updateRequest = UpdateVpnRequest(
      name = Some("updated Name"),
    )
Vpn.update(testVpnId, updateRequest)
```

**Delete a VPN:**

`Vpn.delete(testVpnId)`

**Retrieve a VPN's configuration file:**

`Vpn.downloadVpnConfigurationZIP(testVpnId, "C:/temp")`

### Monitoring Center

**List all usages and alerts of monitoring servers:**

`MonitoringCenter.list()`

To paginate the list of server usages received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of server usages that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of server usages sorted in expected order, pass a server usage property (e.g. `"name"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the usage instances that contain it.

To retrieve a collection of server usages containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,name,status.state"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters Map("sort" -> "-name"), `Map("q" -> "test")` or Map("fields" -> "id,name") is blank, it is ignored in the request.

**Retrieve the usages and alerts for a monitoring server:**

```
 var mc = MonitoringCenter.get(
      fixedServerId,
      Period.LAST_HOUR
    )
```

`period` may be set to `"LAST_HOUR"`, `"LAST_24H"`, `"LAST_7D"`, `"LAST_30D"`, `"LAST_365D"` or `"CUSTOM"`. If `period` is set to `"CUSTOM"`, 

**Retrieve the usages and alerts for a monitoring server for a customer period:**

```
var mc = MonitoringCenter.get(
      fixedServerId,
      Period.CUSTOM,
      today,
      tommorow
    )
```


### Monitoring Policies

**List all monitoring policies:**

`MonitoringPolicy.list()`

To paginate the list of monitoring policies received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of monitoring policies that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of monitoring policies sorted in expected order, pass a monitoring policy property (e.g. `"name"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the monitoring policy instances that contain it.

To retrieve a collection of monitoring policies containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,name,creation_date"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve a single monitoring policy:**

`MonitoringPolicy.get(monitoringPolicyId)`


**Create a monitoring policy:**

```
var thresholdDetailWarningA = ThresholdDetail(
value = 80,
  alert = false
)

var thresholdDetailCriticalA = ThresholdDetail(
  value = 90,
  alert = false
)

var thresholdDetailWarningB = ThresholdDetail(
  value = 1000,
  alert = true
)

var thresholdDetailCriticalB = ThresholdDetail(
  value = 2000,
  alert = true
)

var thresholdItemA = ThresholdItem(
  warning = thresholdDetailWarningA,
  critical = thresholdDetailCriticalA
)

var thresholdItemB = ThresholdItem(
  warning = thresholdDetailWarningB,
  critical = thresholdDetailCriticalB
)

var threshold: Threshold = Threshold(
  cpu = Option(thresholdItemA),
  ram = Option(thresholdItemA),
  internalPing = Option(thresholdItemA),
  transfer = Option(thresholdItemB),
  disk = Option(thresholdItemA)
)

var portRequest = PortRequest(
  protocol = "TCP",
  port = 22,
  alertIf = "RESPONDING",
  emailNotification = false
)

var processRequest = ProcessRequest(
  process = "Test",
  alertIf = "NOT_RUNNING",
  emailNotification = true
)

var createMonitoringPolicyRequest = MonitoringPolicyRequest(
  name = "aaScalaSdkTestMonitoringPolicy1",
  description =
	Option("Test - monitoring policy created using oneandone-cloudserver-sdk-scala."),
  email = "test@test.com",
  agent = true,
  thresholds = threshold,
  ports = Seq(portRequest),
  processes = Seq(processRequest)
)

createdMonitoringPolicy = MonitoringPolicy.create(createMonitoringPolicyRequest)
```
All fields, except `Description`, are required. `AlertIf` property accepts values `"RESPONDING"`/`"NOT_RESPONDING"` for ports, and `"RUNNING"`/`"NOT_RUNNING"` for processes.


**Update a monitoring policy:**

```
var thresholdDetailWarningA = ThresholdDetail(
  value = 85,
  alert = true
)

var thresholdDetailCriticalA = ThresholdDetail(
  value = 95,
  alert = true
)

var thresholdDetailWarningB = ThresholdDetail(
  value = 1500,
  alert = false
)

var thresholdDetailCriticalB = ThresholdDetail(
  value = 1900,
  alert = true
)

var thresholdItemA = ThresholdItem(
  warning = thresholdDetailWarningA,
  critical = thresholdDetailCriticalA
)

var thresholdItemB = ThresholdItem(
  warning = thresholdDetailWarningB,
  critical = thresholdDetailCriticalB
)

var threshold: Threshold = Threshold(
  cpu = Option(thresholdItemA),
  ram = Option(thresholdItemA),
  internalPing = Option(thresholdItemA),
  transfer = Option(thresholdItemB),
  disk = Option(thresholdItemA)
)

var portRequest = PortRequest(
  protocol = "TCP",
  port = 22,
  alertIf = "NOT_RESPONDING",
  emailNotification = true
)

var processRequest = ProcessRequest(
  process = "Test",
  alertIf = "NOT_RUNNING",
  emailNotification = false
)

var updateMonitoringPolicyRequest = MonitoringPolicyRequest(
  name = "aaScalaSdkTestMonitoringPolicyUpdated",
  description =
	Option("Test - monitoring policy updated using oneandone-cloudserver-sdk-scala."),
  email = "test@testing.com",
  agent = true,
  thresholds = threshold,
  ports = Seq(portRequest),
  processes = Seq(processRequest)
)

updatedMonitoringPolicy = MonitoringPolicy.update(
  createdMonitoringPolicyId,
  updateMonitoringPolicyRequest
)
```
All fields of the request are optional. When a threshold is specified in the request, the threshold fields are required.

**Delete a monitoring policy:**

`MonitoringPolicy.delete(createdMonitoringPolicyId)`


**List all ports of a monitoring policy:**

`MonitoringPolicy.listPorts(createdMonitoringPolicyId)`


**Retrieve information about a port of a monitoring policy:**

`MonitoringPolicy.getPort(createdMonitoringPolicyId, portId)`


**Add new ports to a monitoring policy:**

```
 var portRequest = PortRequest(
  protocol = "UDP",
  port = 80,
  alertIf = "NOT_RESPONDING",
  emailNotification = false
)

var response =
  MonitoringPolicy.addPorts(createdMonitoringPolicyId, AddPortsRequest(Seq(portRequest)))
```
Port properties are mandatory.

**Modify a port of a monitoring policy:**

```
var portRequest = PortRequest(
  protocol = "TCP",
  port = 433,
  alertIf = "RESPONDING",
  emailNotification = true
)

var updatePortRequest = UpdatePortRequest(portRequest)

monitoringPolicy = MonitoringPolicy.updatePort(
  createdMonitoringPolicyId,
  monitoringPolicyPortId,
  updatePortRequest
)
```
*Note:* `Protocol` and `Port` cannot be changed.


**Remove a port from a monitoring policy:**

`MonitoringPolicy.deletePort(createdMonitoringPolicyId, monitoringPolicyPortId)`


**List the processes of a monitoring policy:**

`MonitoringPolicy.listProcesses(createdMonitoringPolicyId)`


**Retrieve information about a process of a monitoring policy:**

`MonitoringPolicy.getProcess(createdMonitoringPolicyId, monitoringPolicyProcessesId)`


**Add new processes to a monitoring policy:**

```
 var processRequest = ProcessRequest(
  process = "TestAddProccess",
  alertIf = "RUNNING",
  emailNotification = false
)

var response =
  MonitoringPolicy.addProccesses(
	createdMonitoringPolicyId,
	AddProcessesRequest(Seq(processRequest))
  )
```
All properties of the `MonitoringProcess` instance are required.


**Modify a process of a monitoring policy:**

```
 var processRequest = ProcessRequest(
      process = "TestAddProccessUpdated",
      alertIf = "NOT_RUNNING",
      emailNotification = true
    )

    var updateProcessRequest = UpdateProcessRequest(processRequest)

    monitoringPolicy = MonitoringPolicy.updateProcess(
      createdMonitoringPolicyId,
      monitoringPolicyProcessId,
      updateProcessRequest
    )
```

*Note:* Process name cannot be changed.

**Remove a process from a monitoring policy:**

`MonitoringPolicy.deleteProcess(createdMonitoringPolicyId, monitoringPolicyProcessId)`

**List all servers attached to a monitoring policy:**

`MonitoringPolicy.listServers(createdMonitoringPolicyId)`

**Retrieve information about a server attached to a monitoring policy:**

`MonitoringPolicy.getServer(createdMonitoringPolicyId, fixedServerId)`

**Attach servers to a monitoring policy:**

```
var result = MonitoringPolicy.addServers(
  createdMonitoringPolicyId,
  AddServersRequest(Seq(fixedServerId))
)
```

**Remove a server from a monitoring policy:**

`MonitoringPolicy.deleteServer(createdMonitoringPolicyId, monitoringPolicyServerId)`


### Logs

**List all logs:**

`Log.list(LAST_HOUR)`

`period` can be set to `"LAST_HOUR"`, `"LAST_24H"`, `"LAST_7D"`, `"LAST_30D"`, `"LAST_365D"`.

**List all logs for a custom period:**

`Log.list(CUSTOM, startDate, endDate)`


**Retrieve a single log:**

`log = Log.get(logId)`


### Users

**List all users:**

`User.list()`

To paginate the list of users received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set ` per_page` to the number of users that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of users sorted in expected order, pass a user property (e.g. `"name"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the user instances that contain it.

To retrieve a collection of users containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,name,creation_date,email"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve information about a user:**

`User.get(usersId)`

**Create a user:**

```
 var createUserRequest = CreateUserRequest(
      name = "aaScalaTestUser",
      description = Option("Create a user using oneandone-cloudserver-sdk-scala"),
      password = "scala$Dk1and1UserPaS$worD",
      email = Option("test@scala1cldsrvsdk.com")
    )

createdUser = User.create(createUserRequest)
```

`Name` and `Password` are required parameters. The password must contain at least 8 characters using uppercase letters, numbers and other special symbols.

**Modify a user:**

```
var updateUserApiRequest = UpdateUserApiRequest(
  active = true
)

var userWithUpdatedApi = User.updateApiStatus(createdUserId, updateUserApiRequest)
```

All listed fields in the request are optional. `State` can be set to `"ACTIVE"` or `"DISABLED"`.

**Delete a user:**

`User.delete(createdUserId)`

**Retrieve information about a user's API privileges:**

`User.getUserApi(createdUserId)`

**Retrieve a user's API key:**

`User.getUserApiKey(createdUserId)`

**List IP's from which API access is allowed for a user:**

`User.getUserIps(createdUserId)`

**Add new IP's to a user:**

```
var userIpsRequest = UserIpsRequest(
      ips = ["214.4.143.138"]
    )

User.addUserIps(createdUserId, userIpsRequest)
```

**Modify a user's API privileges:**

```
var updateUserApiRequest = UpdateUserApiRequest(
      active = true
    )

User.updateUserApi(createdUserId, updateUserApiRequest)
```

**Renew a user's API key:**

`User.changeUserApiKey(createdUserId)`

### Roles

**List all roles:**

`Role.list()`

To paginate the list of roles received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set ` per_page` to the number of roles that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less or equal to zero, the parameters are ignored by the framework.

To receive the list of roles sorted in expected order pass a role property (e.g. `"name"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the role instances that contain it.

To retrieve a collection of roles containing only the requested fields pass a list of comma separated properties (e.g. `"id,name,creation_date"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve information about a role:**

`Role.get(rolesId)`

**Create a role:**

```
 var createRoleRequest = CreateRoleRequest("ScalaTestRole")
Role.create(createRoleRequest)
```

**Clone a role:**

```
var cloneRoleRequest = CloneRoleRequest("ClonedScalaTestRole")
clonedRole = Role.clone(createdRoleId, cloneRoleRequest)
```

**Modify a role:**

```
var updateRoleRequest = UpdateRoleRequest(
     name = Option("ScalaTestRoleUpdated")
   )
Role.update(createdRoleId, updateRoleRequest)
```

`ACTIVE` and `DISABLE` are valid values for the state.

**Delete a role:**

` Role.delete(createdRoleId)`

**Retrieve information about a role's permissions:**

`Role.getRolePermissions(createdRoleId)`

**Modify a role's permissions:**

```
var permissionDetails = PermissionDetails(
      create = Option(true),
      delete = Option(true),
      show = Option(true)
    )
    var permissions = Permission(
      roles = Option(permissionDetails),
      servers = Option(permissionDetails),
      users = Option(permissionDetails)
    )

Role.updateRolePermissions(createdRoleId, permissions)
```

**Assign users to a role:**

```
Role.addUsersToRole(createdRoleId, usersList)
```

`usersList` is a String List of user ID's.

**List a role's users:**

`Role.getRoleUsers(createdRoleId)`

**Retrieve information about a role's user:**

`Role.getRoleUser(createdRoleId, userId)`

**Remove a role's user:**

`Role.removeRoleUser(createdRoleId, userId)`


### Usages

**List your usages:**

`Usages.list(LAST_7D)`

`period` can be set to `"LAST_HOUR"`, `"LAST_24H"`, `"LAST_7D"`, `"LAST_30D"`, `"LAST_365D"` .

**List your usages for a custom period:**

`Usages.list(CUSTOM, twentydaysago, tommorow)`

### Server Appliances

**List all the appliances that you can use to create a server:**

`ServerAppliance.list()`

To paginate the list of server appliances received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of server appliances that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of server appliances sorted in expected order, pass a server appliance property (e.g. `"os"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the server appliance instances that contain it.

To retrieve a collection of server appliances containing only the requested fields, pass a list of comma separated properties (e.g. `"id,os,architecture"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters Map("sort" -> "-name"), `Map("q" -> "test")` or Map("fields" -> "id,name") is blank, it is ignored in the request.

**Retrieve information about specific appliance:**

` ServerAppliance.get(serverAppliancesId)`


### DVD ISO

**List all operative systems and tools that you can load into your virtual DVD unit:**

`DvdIso.list()`

To paginate the list of ISO DVDs received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of ISO DVDs that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of ISO DVDs sorted in expected order, pass a ISO DVD property (e.g. `"type"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the ISO DVD instances that contain it.

To retrieve a collection of ISO DVDs containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,name,type"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters Map("sort" -> "-name"), `Map("q" -> "test")` or Map("fields" -> "id,name") is blank, it is ignored in the request.

**Retrieve a specific ISO image:**

`DvdIso.get(dvdIsosId)`

### Ping

**Check if 1&amp;1 REST API is running:**

` Ping.get()`

If the API is running, the response is an  Either `PONG,INACTIVE`.

**Validate if 1&amp;1 REST API is running and the authorization token is valid:**

`PingAuth.get()`

The response is an Either `PONG`. if the API is running and the token is valid.


### Pricing

**Show prices for all available resources in the Cloud Panel:**

`Price.get()`


### Data Centers

**List all 1&amp;1 Cloud Server data centers:**

`Datacenter.list()`

**Retrieve a specific data center:**

`Datacenter.get(datacenters(0)Id)`

### Block Storages

**List block storages:**

`Blockstorage.list()`

To paginate the list of block storages received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set `Map("per_page" -> "1")` to the number of block storages that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less than or equal to zero, the parameters are ignored by the framework.

To receive the list of block storages sorted in expected order, pass a volume property (e.g. `"name"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the block storage instances that contain it.

To retrieve a collection of block storages containing only the requested fields, pass a list of comma-separated properties (e.g. `"id,name,size"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve a block storage:**

`Blockstorage.get(testBsId)`


**Create a block storage:**

```
 var request = BlockstorageRequest(
      name = "scala test bs",
      size = 60,
      datacenterId = datacenterId
    )

Blockstorage.create(request)
```

`Description` is optional parameter.


**Update a block storage:**

```
var updateRequest = UpdateBlockstorageRequest(
  name = Some("updated name")
)

Blockstorage.update(testBsId, updateRequest)
```
All request's parameters are optional.


**Remove a block storage:**

`Blockstorage.delete(testBsId)`


**Attach a block storage to a server:**

```
Blockstorage.attachServer(testBs.id, fixedServer.id)
```

				
**Detach a block storage from a server:**

`Blockstorage.detachServer(testBs.id)`

### SSH Keys

**List all SshKeys:**

`SshKey.list()`

To paginate the list of SshKeys received in the response use `Map("page" -> "1")` and `Map("per_page" -> "1")` parameters. Set ` per_page` to the number of SSH Keys that will be shown in each page. `Map("page" -> "1")` indicates the current page. When set to an integer value that is less or equal to zero, the parameters are ignored by the framework.

To receive the list of SshKeys sorted in expected order pass an SshKey property (e.g. `"name"`) in Map("sort" -> "-name") parameter. Prefix the sorting attribute with `-` sign for sorting in descending order.

Use `Map("q" -> "test")` parameter to search for a string in the response and return only the SshKey instances that contain it.

To retrieve a collection of SshKeys containing only the requested fields pass a list of comma separated properties (e.g. `"id,name,creation_date"`) in `Map("fields" -> "id,name")` parameter.

If any of the parameters `Map("sort" -> "")`, `Map("q" -> "")` or `Map("fields" -> "")` is set to an empty string, it is ignored in the request.

**Retrieve information about an SshKey:**

`SshKey.get(createdSshKey.id)`

**Create an SskKey:**

```
var request = CreateSshKeyRequest(
      name = "aaScalaSshKeyTest",
      description = Option("Testing the creation of ssh key using oneandone-cloudserver-sdk-scala"),
      publicKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDLI86QSeZTbL+ZX0ycke59QetHoTv52puyw50eSgTxmQPpigttjyhKPNBJ/RHwHyD3+/5dWDQRjUwhYfmxqQgA3bYKNggsP1/Eiv/baLW7S9l2w7eVpdR/gSIOjSK5JhHsMsaCb4kJUssxLRv"
    )
SshKey.create(request)
```

**Modify an SshKey:**

```
var updateRequest = UpdateSshKeyRequest(
      name = "aaScalaSshKeyTestUpdated",
      description = Option("Testing the update of ssh key using oneandone-cloudserver-sdk-scala")
    )
SshKey.update(createdSshKey.id, updateRequest)
```

**Delete an SshKey:**

`SshKey.delete(createdSshKey.id)`


## Example

The example below is a main class in Scala that creates an IP, firewall policy, and a load balancer. After that it creates a server and waits for it to deploy and power on.

After the server is created we assign the firewall policy and the load balancer to the server and in the end we clean everything out.

```Scala
package example
import oneandone.firewallpolicies.{FirewallPolicy, FirewallPolicyRequest, Protocol, RuleRequest}
import oneandone.loadbalancers._
import oneandone.publicips.{IPType, PublicIp, PublicIpRequest}
import oneandone.servers._

class Example {
  implicit val client                = oneandone.OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var datacenters                    = oneandone.datacenters.Datacenter.list()
  var firewallPolicy: FirewallPolicy = null
  var loadBalancer: LoadBalancer     = null
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
    firewallPolicy = FirewallPolicy.create(request)
    FirewallPolicy.waitStatus(firewallPolicy.id, GeneralState.ACTIVE)

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
    loadBalancer = LoadBalancer.createLoadbalancer(lbRequest)
    LoadBalancer.waitLoadbalancerStatus(loadBalancer.id, GeneralState.ACTIVE)

    //create and reserve a public ip
    var ipRequest = PublicIpRequest(
      `type` = Some(IPType.IPV4)
    )

    println("creating a public ip")
    exampleIp = PublicIp.create(ipRequest)
    PublicIp.waitStatus(exampleIp.id, GeneralState.ACTIVE)

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
    Server.waitStatus(exampleServer.id, ServerState.POWERED_ON)

    println("Server information", Server.get(exampleServer.id))

    //cleanup
    Server.delete(exampleServer.id)
    Server.waitDeleted(exampleServer.id)
    FirewallPolicy.delete(firewallPolicy.id)
    LoadBalancer.delete(loadBalancer.id)
    PublicIp.delete(exampleIp.id)

  }

}
```