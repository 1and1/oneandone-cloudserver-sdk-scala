package oneandone.roles

case class UpdateRoleRequest(
    name: Option[String] = None,
    description: Option[String] = None,
    state: Option[String] = None
) {}
