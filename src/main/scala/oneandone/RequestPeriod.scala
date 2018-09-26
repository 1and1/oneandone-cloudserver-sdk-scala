 package oneandone

 case class RequestPeriod() {}

 object RequestPeriod extends Enumeration {
   type RequestPeriod = Value
   val LAST_HOUR = Value("LAST_HOUR")
   val LAST_24H = Value("LAST_24H")
   val LAST_7D = Value("LAST_7D")
   val LAST_30D = Value("LAST_30D")
   val LAST_365D = Value("LAST_365D")
   val CUSTOM = Value("CUSTOM")
 }