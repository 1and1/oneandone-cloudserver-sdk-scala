package oneandone

import oneandone.sshkeys.{SshKey, CreateSshKeyRequest, UpdateSshKeyRequest}
import org.scalatest.FunSuite
import java.security.KeyPairGenerator

class SshKeyTest extends FunSuite {
  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var sshKeys: Seq[SshKey] = Seq.empty
  var sshKey, createdSshKey, updatedSshKey, deletedSshKey: SshKey = null

  test("Create SSH Key") {
    var request = CreateSshKeyRequest(
      name = "aaScalaSshKeyTest",
      description = Option("Testing the creation of ssh key using oneandone-cloudserver-sdk-scala"),
      publicKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDLI86QSeZTbL+ZX0ycke59QetHoTv52puyw50eSgTxmQPpigttjyhKi7dEKK0ZbfQh0B18mbT7UMT9/10g1FmeNm/PNBJ/RHwHyD3+MM4kxlsIdYcTj54Rvh37Ci2XGpdYqnTnYs8nYf1BUyF4NuP8L64ki/NfMlA263b6pqW+nMU8j/th2vzY8ZxYchwemxlL3hmZvaMTmlef2gEuo1pkguGhpPu5LRrl8NKfFq4CNjWvMNA6yA1DXyN095+NbCRwey6bZI/5dWDQRjUwhYfmxqQgA3bYKNggsP1/Eiv/baLW7S9l2w7eVpdR/gSIOjSK5JhHsMsaCb4kJUssxLRv"
    )

    createdSshKey = SshKey.create(request)
    assert(true == SshKey.waitStatus(createdSshKey.id, "POWERED_ON"))
  }

  test("List SSH Keys") {
    sshKeys = SshKey.list()
    assert(sshKeys.size > 0)
  }

  test("Get SSH Key") {
    sshKey = SshKey.get(createdSshKey.id)
    assert(sshKey.id == createdSshKey.id)
  }

  test("Update SSH Key") {
    var updateRequest = UpdateSshKeyRequest(
      name = "aaScalaSshKeyTestUpdated",
      description = Option("Testing the update of ssh key using oneandone-cloudserver-sdk-scala")
    )
    var updatedSshKey = SshKey.update(createdSshKey.id, updateRequest)

    assert(updatedSshKey.name == "aaScalaSshKeyTestUpdated")
    assert(updatedSshKey.description == Option("Testing the update of ssh key using oneandone-cloudserver-sdk-scala"))
    assert(true == SshKey.waitStatus(updatedSshKey.id, "POWERED_ON"))
  }

  test("Delete SSH Key") {
    var deletedSshKey = SshKey.delete(createdSshKey.id)
    assert(deletedSshKey != null)
    assert(deletedSshKey.state == "DELETING")
  }
}