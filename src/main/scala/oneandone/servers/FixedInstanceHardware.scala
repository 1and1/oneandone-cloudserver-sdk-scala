package oneandone.servers

case class FixedInstanceHardware(
    fixedInstanceSizeId: Option[String] = None,
    vcore: Option[Double] = None,
    coresPerProcessor: Option[Double] = None,
    ram: Option[Double] = None,
    hdds: Seq[HddModel]
)
