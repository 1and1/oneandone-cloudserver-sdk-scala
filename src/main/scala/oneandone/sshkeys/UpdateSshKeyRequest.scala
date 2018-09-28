package oneandone.sshkeys

case class UpdateSshKeyRequest(
    name: String,
    description: Option[String]
) {}
