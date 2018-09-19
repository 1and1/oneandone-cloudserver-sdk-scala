package oneandone

case class Types() {

  object ServerStatus extends Enumeration {
    type ServerStatus = Value

    val PoweredOn  = Value("POWERED_ON")
    val PoweredOff = Value("POWERED_OFF")
    val Rebooting      = Value("REBOOTING")
  }
}
