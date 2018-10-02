package oneandone.sharedstorages
import oneandone.sharedstorages.SharedStorageRights.SharedStorageRights

case class SharedstorageServer(
    id: String,
    name: String,
    rights: SharedStorageRights
) {}

object SharedStorageRights extends Enumeration {
  type SharedStorageRights = Value
  val Read      = Value("R")
  val ReadWrite = Value("RW")
}
