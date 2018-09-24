package oneandone.prices

case class FlexibleServerPrice(
  name: String,
  priceNet: Double,
  priceGross: Double,
  unit: String
) {}