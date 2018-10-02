package oneandone.servers
import oneandone.servers.ServerState.ServerState

case class Status(
    state: ServerState,
    percent: Option[Integer] = null
)

object ServerState extends Enumeration {
  type ServerState = Value
  val POWERING_ON  = Value("POWERING_ON")
  val POWERED_ON   = Value("POWERED_ON")
  val POWERED_OFF  = Value("POWERED_OFF")
  val POWERING_OFF = Value("POWERING_OFF")
  val DEPLOYING    = Value("DEPLOYING")
  val REBOOTING    = Value("REBOOTING")
  val REMOVING     = Value("REMOVING")
  val CONFIGURING  = Value("CONFIGURING")
  val ON_RECOVERY  = Value("ON_RECOVERY")

}

object ServerAction extends Enumeration {
  type ServerAction = Value
  val POWER_ON  = Value("POWER_ON")
  val POWER_OFF = Value("POWER_OFF")
  val REBOOT    = Value("REBOOT")
}

object ActionMethod extends Enumeration {
  type ActionMethod = Value
  val SOFTWARE  = Value("SOFTWARE")
  val HARDWARE = Value("HARDWARE")
}

object GeneralState extends Enumeration {
  type GeneralState = Value
  val ACTIVE  = Value("ACTIVE")
  val CONFIGURING = Value("CONFIGURING")
}
