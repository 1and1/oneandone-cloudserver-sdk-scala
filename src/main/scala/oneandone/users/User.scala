package oneandone.users

import oneandone.{BasicResource, OneandoneClient}
import oneandone.roles.Permission

import org.json4s.{DefaultFormats, Extraction, Formats}
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

case class User(
    id: String,
    name: String,
    description: Option[String],
    email: Option[String],
    state: String,
    creationDate: String,
    api: Api,
    role: BasicResource
) {}

object User extends oneandone.Path {
  override val path: Seq[String] = Seq("users")
  var UserApiPath = "api"
  var UserApiKeyPath = "api/key"
  var UserIpsPath = "api/ips"
  var CurrentUserPermissionsPath = "current_user_permissions"

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[User] = {
    val response = client.get(path, queryParameters)
    val json = parse(response).camelizeKeys
    json.extract[Seq[User]]
  }

  def get(id: String)(implicit client: OneandoneClient): User = {
    val response = client.get(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[User]
  }

  def createUser(
      request: CreateUserRequest
  )(implicit client: OneandoneClient): User = {
    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[User]
  }

  def delete(id: String)(implicit client: OneandoneClient): User = {
    val response = client.delete(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[User]
  }

  def getUserApi(id: String)(implicit client: OneandoneClient): Api = {
    val response = client.get(path :+ id :+ UserApiPath)
    val json = parse(response).camelizeKeys
    json.extract[Api]
  }

  def updateUserApi(
      userId: String,
      request: UpdateUserApiRequest
  )(implicit client: OneandoneClient): User = {
    val response = client.put(
      path :+ userId :+ UserApiPath,
      Extraction.decompose(request).snakizeKeys
    )
    val json = parse(response).camelizeKeys
    json.extract[User]
  }

  def getUserApiKey(id: String)(implicit client: OneandoneClient): ApiKey = {
    val response = client.get(path :+ id :+ UserApiKeyPath)
    val json = parse(response).camelizeKeys
    json.extract[ApiKey]
  }

  def changeUserApiKey(id: String)(implicit client: OneandoneClient): User = {
    val response = client.get(path :+ id :+ UserApiKeyPath)
    val json = parse(response).camelizeKeys
    json.extract[User]
  }

  def addUserIps(id: String, userIps: UserIpsRequest)(implicit client: OneandoneClient): User = {
    val response = client.post(path :+ id :+ UserIpsPath, Extraction.decompose(userIps).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[User]
  }

  def getUserIps(id: String)(implicit client: OneandoneClient): Seq[String] = {
    val response = client.get(path :+ id :+ UserIpsPath)
    val json = parse(response).camelizeKeys
    json.extract[Seq[String]]
  }

  def deleteUserIp(userId: String, ipId: String)(implicit client: OneandoneClient): User = {
    val response = client.delete(path :+ userId :+ UserIpsPath :+ ipId)
    val json = parse(response).camelizeKeys
    json.extract[User]
  }

  def getCurrentUserPermissions()(implicit client: OneandoneClient): Permission = {
    val response = client.get(path :+ CurrentUserPermissionsPath)
    val json = parse(response).camelizeKeys
    json.extract[Permission]
  }

  def waitUserStatus(id: String, status: String)(
      implicit client: OneandoneClient
  ): Boolean = {
    Thread.sleep(2000)
    var response = client.get(path :+ id)
    var json = parse(response).camelizeKeys
    var result = json.extract[User]
    while (result.state != status) {
      Thread.sleep(1000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      result = json.extract[User]
    }
    true
  }
}
