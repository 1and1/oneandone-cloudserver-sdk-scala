package oneandone.servers

case class CloneServerRequest(
    name: String,
    datacenterId: Option[String] = None
) {}
