package oneandone

import oneandone.users.{Api, CreateUserRequest, UpdateUserApiRequest, User}
import oneandone.roles.{Permission, PermissionDetails}
import oneandone.servers.GeneralState
import org.scalatest.FunSuite

class UserTest extends FunSuite {
  implicit val client         = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var users: Seq[User]        = Seq.empty
  var user, createdUser: User = null
  var permission: Permission  = null

  test("List Users") {
    users = User.list()

    assert(users.size > 0)
  }

  test("Get User By Id") {
    user = User.get(users(0).id)

    assert(user.id == users(0).id)
  }

  test("Create a User") {
    var createUserRequest = CreateUserRequest(
      name = "aaScalaTestUser",
      description = Option("Create a user using oneandone-cloudserver-sdk-scala"),
      password = "scala$Dk1and1UserPaS$worD",
      email = Option("test@scala1cldsrvsdk.com")
    )

    createdUser = User.create(createUserRequest)
    assert(true == User.waitStatus(createdUser.id, GeneralState.ACTIVE))
  }

  test("Get Current User Permissions") {
    permission = User.getCurrentUserPermissions()

    assert(permission != null)
  }

//  IMPORTANT NOTE:
//  If you execute the UserTest suite, please DELETE the created user manually via 1&1 Cloud Panel.
//
//  Currently, allowing the user API access and deleting a user is not allowed outside of 1&1 Cloud Panel.
//  The tests below are written for demonstration purposes and in case the above mentioned restriction
//  changes in the future.
//
//  test("Update User Api") {
//    var updateUserApiRequest = UpdateUserApiRequest(
//      active = true
//    )
//
//    var userWithUpdatedApi = User.updateUserApi(createdUser.id, updateUserApiRequest)
//    assert(true == User.waitUserStatus(userWithUpdatedApi.id, "ACTIVE"))
//  }
//
//  test("Get User Api") {
//    var userApi = User.getUserApi(createdUser.id)
//
//    assert(userApi != null)
//    assert(userApi.active == false)
//  }
//
//  test("Get User Api Key") {
//    var userApiKey = User.getUserApiKey(createdUser.id)
//
//    assert(userApiKey != null)
//  }
//
//  test("Change User Api Key") {
//    var user = User.changeUserApiKey(createdUser.id)
//
//    assert(user != null)
//  }
//
//  test("Add User Ips") {
//    var userIpsRequest = UserIpsRequest(
//      ips = ["214.4.143.138"]
//    )
//
//    var userWithAddedIps = User.addUserIps(createdUser.id, userIpsRequest)
//    assert(true == User.waitUserStatus(userIpsRequest.id, "ACTIVE"))
//  }
//
//  test("Get User Ips") {
//    var userIps = User.getUserIps(createdUser.id)
//
//    assert(userIps != null)
//  }
//
//  test("Delete a User") {
//    println(createdUser)
//    var deletedUser = User.delete(createdUser.id)
//
//    assert(deletedUser.state == "REMOVING")
//  }
}
