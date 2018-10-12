package oneandone

import oneandone.roles.{
  CloneRoleRequest,
  CreateRoleRequest,
  Permission,
  PermissionDetails,
  Role,
  UpdateRoleRequest
}
import oneandone.servers.GeneralState
import org.scalatest.FunSuite

class RoleTest extends FunSuite {

  implicit val client                     = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var roles: Seq[Role]                    = Seq.empty
  var role, createdRole, clonedRole: Role = null

  test("List Roles") {
    roles = Role.list()

    assert(roles.size > 0)
    assert(roles(0).id != null)
  }

  test("Get a Role") {
    role = Role.get(roles(0).id)

    assert(role.id == roles(0).id)
  }

  test("Create a Role") {
    var createRoleRequest = CreateRoleRequest("ScalaTestRole")

    createdRole = Role.create(createRoleRequest)
    assert(true == Role.waitStatus(createdRole.id, "ACTIVE"))
  }

  test("Update a Role") {
    var updateRoleRequest = UpdateRoleRequest(
      name = Option("ScalaTestRoleUpdated")
    )
    var updatedRole = Role.update(createdRole.id, updateRoleRequest)
    assert(updatedRole.name == "ScalaTestRoleUpdated")
    Role.waitStatus(updatedRole.id, "ACTIVE")
  }

  test("Update Role Permissions") {
    var permissionDetails = PermissionDetails(
      create = Option(true),
      delete = Option(true),
      show = Option(true)
    )
    var permissions = Permission(
      roles = Option(permissionDetails),
      servers = Option(permissionDetails),
      users = Option(permissionDetails)
    )

    var updateRolePermissonsResponse = Role.updateRolePermissions(createdRole.id, permissions)
    assert(updateRolePermissonsResponse != null)
  }

  test("Get Role Permissions") {
    var rolePermission = Role.getRolePermissions(createdRole.id)

    assert(rolePermission != null)
  }

  test("Clone a Role") {
    var cloneRoleRequest = CloneRoleRequest("ClonedScalaTestRole")
    clonedRole = Role.clone(createdRole.id, cloneRoleRequest)

    assert(clonedRole != null)
    assert(true == Role.waitStatus(clonedRole.id, "ACTIVE"))
  }

  test("Delete a Role") {
    var deletedRole = Role.delete(createdRole.id)
    Role.delete(clonedRole.id)

    assert(deletedRole.state == "REMOVING")

  }
}
