package oneandone
import oneandone.blockstorages.StorageState
import oneandone.firewallpolicies.Protocol
import oneandone.images._
import oneandone.loadbalancers._
import oneandone.publicips._
import oneandone.servers._
import oneandone.sharedstorages.SharedStorageRights
import org.json4s.{DefaultFormats, Formats}
import org.json4s.ext.EnumNameSerializer

private[oneandone] trait Path {
  protected val path: Seq[String]
  implicit lazy val serializerFormats: Formats = DefaultFormats + new EnumNameSerializer(
    ServerState) + new EnumNameSerializer(ServerAction) + new EnumNameSerializer(GeneralState) + new EnumNameSerializer(
    RequestPeriod) + new EnumNameSerializer(Protocol) + new EnumNameSerializer(SharedStorageRights) + new EnumNameSerializer(
    StorageState) + new EnumNameSerializer(IPType) + new EnumNameSerializer(AssignedToType) + new EnumNameSerializer(
    LoadBalancerProtocol) + new EnumNameSerializer(Method) + new EnumNameSerializer(HealthCheckTest) + new EnumNameSerializer(
    Frequency) + new EnumNameSerializer(OsImageType) + new EnumNameSerializer(ImageType) + new EnumNameSerializer(
    ActionMethod) + new EnumNameSerializer(ServerAction)
}
