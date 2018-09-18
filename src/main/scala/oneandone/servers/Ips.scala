package oneandone.servers

case class Ips(
    id: String,
    ip: String,
    `type`: String,
    reverseDns: String,
    main: Boolean
) {}
