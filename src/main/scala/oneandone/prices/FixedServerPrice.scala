package oneandone.prices

import oneandone.servers.Hardware

case class FixedServerPrice(
  name: String,
  priceNet: Double,
  priceGross: Double,
  unit: String,
  hardware: Hardware
) {}