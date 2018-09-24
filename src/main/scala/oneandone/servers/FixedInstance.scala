package oneandone.servers

case class FixedInstance(
    name: String,
    id: String,
    hardware: FixedInstanceHardware
) {}
