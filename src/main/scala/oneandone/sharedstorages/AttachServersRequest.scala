package oneandone.sharedstorages

case class AttachServersRequest(
    servers: Seq[SharedStorageServerRequest]
) {}
