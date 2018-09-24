package oneandone.prices

case class SharedStoragePrice(
  name: String,
  priceNet: Double,
  priceGross: Double,
  unit: String
) {}