package oneandone.users

case class CreateUserRequest(
    name: String,
    description: Option[String],
    password: String,
    email: Option[String]
) {}
