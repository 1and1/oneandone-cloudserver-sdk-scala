package oneandone.prices

case class ServerPrice(
  fixedServers: List[FixedServerPrice],
  flexibleServer: List[FlexibleServerPrice]
) {}