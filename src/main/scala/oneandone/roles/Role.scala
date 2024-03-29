package oneandone.roles

import oneandone.{BasicResource, BooleanCustomSerializer, OneandoneClient, Path}
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods._
import org.json4s.Extraction

case class Role(
    id: String,
    name: String,
    description: Option[String],
    state: String,
    default: Integer,
    creationDate: String,
    users: Seq[BasicResource],
    permissions: Permission
) {}

object Role extends Path {
  override val path: Seq[String] = Seq("roles")
  val serializers = List(BooleanCustomSerializer)
  override implicit lazy val serializerFormats: Formats = DefaultFormats ++ serializers
  var PermissionsPath = "permissions"
  var UsersPath = "users"
  var ClonePath = "clone"

  def list()(implicit client: OneandoneClient): Seq[Role] = {
    val response = client.get(path)
    val json = parse(response).camelizeKeys

    json.extract[Seq[Role]]
  }

  def get(id: String)(implicit client: OneandoneClient): Role = {
    val response = client.get(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[Role]
  }

  def create(
      request: CreateRoleRequest
  )(implicit client: OneandoneClient): Role = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Role]
  }

  def update(id: String, request: UpdateRoleRequest)(
      implicit client: OneandoneClient
  ): Role = {
    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Role]
  }

  def delete(id: String)(implicit client: OneandoneClient): Role = {
    val response = client.delete(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[Role]
  }

  def updateRolePermissions(roleId: String, permissions: Permission)(
      implicit client: OneandoneClient
  ): Role = {
    var request = (permissions)
    val response = client.put(
      path :+ roleId :+ PermissionsPath,
      Extraction.decompose(request).snakizeKeys
    )

    val json = parse(response).camelizeKeys
    json.extract[Role]
  }

  def getRolePermissions(roleId: String)(implicit client: OneandoneClient): Permission = {
    val response = client.get(path :+ roleId :+ PermissionsPath)
    val json = parse(response).camelizeKeys
    json.extract[Permission]
  }

  def getRoleUsers(roleId: String)(implicit client: OneandoneClient): Seq[BasicResource] = {
    val response = client.get(path :+ roleId :+ UsersPath)
    val json = parse(response).camelizeKeys
    json.extract[Seq[BasicResource]]
  }

  def addUsersToRole(roleId: String, userIds: Seq[String])(implicit client: OneandoneClient): Role = {
    val response = client.post(path :+ roleId :+ UsersPath, Extraction.decompose(userIds).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Role]
  }

  def getRoleUser(roleId: String, userId: String)(implicit client: OneandoneClient): BasicResource = {
    val response = client.get(path :+ roleId :+ UsersPath:+ userId)
    val json = parse(response).camelizeKeys
    json.extract[BasicResource]
  }

  def removeRoleUser(roleId: String, userId: String)(implicit client: OneandoneClient): Role = {
    val response = client.delete(path :+ roleId :+ UsersPath :+ userId)
    val json     = parse(response).camelizeKeys
    json.extract[Role]
  }

  def clone(
      roleId: String,
      request: CloneRoleRequest
  )(implicit client: OneandoneClient): Role = {

    val response = client.post(path :+ roleId :+ ClonePath, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Role]
  }

  def waitStatus(id: String, status: String)(
      implicit client: OneandoneClient
  ): Boolean = {
    var response = client.get(path :+ id)
    var json = parse(response).camelizeKeys
    var result = json.extract[Role]
    while (result.state != status) {
      Thread.sleep(1000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      result = json.extract[Role]
    }
    true
  }
}
