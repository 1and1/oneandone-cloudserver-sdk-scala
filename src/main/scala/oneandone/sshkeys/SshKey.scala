package oneandone.sshkeys

import oneandone.servers.GeneralState.GeneralState
import oneandone.{BasicResource, BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods._
import org.json4s.Extraction

case class SshKey(
    id: String,
    name: String,
    description: Option[String],
    state: String,
    servers: Option[Seq[BasicResource]],
    md5: Option[String],
    publicKey: String,
    creationDate: String
) {}

object SshKey extends Path {
  override val path: Seq[String] = Seq("ssh_keys")

  def list()(implicit client: OneandoneClient): Seq[SshKey] = {
    val response = client.get(path)
    val json     = parse(response).camelizeKeys

    json.extract[Seq[SshKey]]
  }

  def get(id: String)(implicit client: OneandoneClient): SshKey = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[SshKey]
  }

  def create(request: CreateSshKeyRequest)(implicit client: OneandoneClient): SshKey = {
    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[SshKey]
  }

  def update(id: String, request: UpdateSshKeyRequest)(
      implicit client: OneandoneClient
  ): SshKey = {
    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[SshKey]
  }

  def delete(id: String)(implicit client: OneandoneClient): SshKey = {
    val response = client.delete(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[SshKey]
  }

  def waitStatus(id: String, status: String)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var result   = json.extract[SshKey]
    while (result.state != status) {
      Thread.sleep(1000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      result = json.extract[SshKey]
    }
    true
  }
}
