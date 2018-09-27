package oneandone

import oneandone.roles.{Role, CreateRoleRequest, UpdateRoleRequest, CloneRoleRequest, Permission, PermissionDetails}
import org.scalatest.FunSuite

class RoleTest extends FunSuite {

  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var roles: Seq[Role] = Seq.empty
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

    createdRole = Role.createRole(createRoleRequest)
    assert(true == Role.waitRoleStatus(createdRole.id, "ACTIVE"))
  }

  test("Update a Role") {
    var updateRoleRequest = UpdateRoleRequest(
      name = Option("ScalaTestRoleUpdated")
    )
    var updatedRole = Role.updateRole(createdRole.id, updateRoleRequest)
    assert(updatedRole.name == "ScalaTestRoleUpdated")
    Role.waitRoleStatus(updatedRole.id, "ACTIVE")
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
    var clonedRole = Role.cloneRole(createdRole.id, cloneRoleRequest)

    assert(clonedRole != null)
    assert(true == Role.waitRoleStatus(clonedRole.id, "ACTIVE"))
  }

  test("Delete a Role") {
    var deletedRole = Role.delete(createdRole.id)

    assert(deletedRole.state == "REMOVING")
  }
}