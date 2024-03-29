package oneandone.servers

case class Hardware(
    baremetalModelId: Option[String] = None,
    fixedInstanceSizeId: Option[String] = None,
    vcore: Option[Double] = None,
    coresPerProcessor: Option[Double] = None,
    ram: Option[Double] = None,
    hdds: Option[Seq[Hdds]] = None
)
