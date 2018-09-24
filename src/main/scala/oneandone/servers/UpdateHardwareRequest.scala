package oneandone.servers

case class UpdateHardwareRequest(
    fixedInstanceSizeId: Option[String] = None,
    vcore: Option[Double] = None,
    coresPerProcessor: Option[Double] = None,
    ram: Option[Double] = None
)
