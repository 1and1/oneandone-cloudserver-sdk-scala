package oneandone.users

case class UserIpsRequest(
    ips: Option[Seq[String]]
) {}
