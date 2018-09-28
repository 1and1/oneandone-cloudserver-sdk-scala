package oneandone.sshkeys

case class CreateSshKeyRequest(
    name: String,
    description: Option[String],
    publicKey: String
) {}
