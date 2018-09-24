package oneandone
import oneandone.prices.Price
import org.scalatest.FunSuite

class PriceTest extends FunSuite {

  implicit val client = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var price: Price = null

  test("Get Pricing Plans") {
    price = Price.get()

    assert(price != null)
    assert(!price.pricingPlans.image.isEmpty)
    assert(!price.pricingPlans.sharedStorage.isEmpty)
    assert(!price.pricingPlans.softwareLicences.isEmpty)
    assert(!price.pricingPlans.publicIps.isEmpty)
    assert(!price.pricingPlans.servers.isEmpty)
    assert(!price.pricingPlans.servers.get.flexibleServer.isEmpty)
    assert(!price.pricingPlans.servers.get.fixedServers.isEmpty)
  }
}
