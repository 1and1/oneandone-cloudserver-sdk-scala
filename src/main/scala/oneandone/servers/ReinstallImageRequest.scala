package oneandone.servers

case class ReinstallImageRequest(
    name: String,
    password: Option[String] = None,
    sshPassword: Option[Boolean] = None,
    publicKey: Option[Seq[String]] = None,
    rsaKey: Option[String] = None,
) {}
