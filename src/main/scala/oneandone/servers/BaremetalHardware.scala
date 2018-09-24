package oneandone.servers

case class BaremetalHardware(
    core: Option[Double] = None,
    coresPerProcessor: Option[Double] = None,
    ram: Option[Double] = None,
    unit: Option[String] = None,
    hdds: Option[Hdds] = None
) {}
