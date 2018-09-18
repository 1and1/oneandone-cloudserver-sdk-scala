package oneandone.servers
import java.util.Optional

case class Hardware(
    baremetalModelId: Option[String] = None,
    fixedInstanceSizeId: Option[String] = None,
    vCore: Option[Double] = None,
    coresPerProcessor: Option[Double] = None,
    ram: Option[Double] = None,
    hdds: Option[List[Hdds]] = None
)
