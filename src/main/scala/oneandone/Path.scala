package oneandone
import org.json4s.{DefaultFormats, Formats}

private[oneandone] trait Path {
  protected val path: Seq[String]
  implicit lazy val serializerFormats: Formats = DefaultFormats
}
