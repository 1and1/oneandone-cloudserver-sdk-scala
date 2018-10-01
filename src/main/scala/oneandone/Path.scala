package oneandone
import oneandone.servers.{GeneralState, ServerAction, ServerState}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.ext.EnumNameSerializer

private[oneandone] trait Path {
  protected val path: Seq[String]
  implicit lazy val serializerFormats: Formats = DefaultFormats + new EnumNameSerializer(
    ServerState) + new EnumNameSerializer(ServerAction) + new EnumNameSerializer(GeneralState)
}
