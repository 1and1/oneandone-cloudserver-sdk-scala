package oneandone.prices

case class PublicIpPrice(
  name: String,
  priceNet: Double,
  priceGross: Double,
  unit: String
) {}