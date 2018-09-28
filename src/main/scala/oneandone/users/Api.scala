package oneandone.users

case class Api(
    active: Boolean,
    key: Option[String],
    allowedIps: Option[Seq[String]]
) {}
