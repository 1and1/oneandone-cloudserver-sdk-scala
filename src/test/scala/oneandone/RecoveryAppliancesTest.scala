package oneandone

import oneandone.recoveryappliances.RecoveryAppliance
import org.scalatest.FunSuite

class RecoveryAppliancesTest extends FunSuite {

  implicit val client      = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var recoveryAppliances: Seq[RecoveryAppliance] = Seq.empty

  test("List Recovery Appliances") {
    recoveryAppliances = RecoveryAppliance.list()
    assert(recoveryAppliances.size > 0)
  }

  test("Get a Recovery Appliance") {
    var recoveryAppliance = RecoveryAppliance.get(recoveryAppliances(0).id)
    assert(recoveryAppliance.id == recoveryAppliances(0).id)
  }

}