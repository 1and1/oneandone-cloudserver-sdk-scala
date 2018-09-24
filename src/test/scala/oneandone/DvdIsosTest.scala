package oneandone

import oneandone.dvdisos.DvdIso
import org.scalatest.FunSuite

class DvdIsosTest extends FunSuite {

  implicit val client      = OneandoneClient(sys.env("ONEANDONE_TOKEN"))
  var dvdIsos: Seq[DvdIso] = Seq.empty

  test("List DVD ISOs") {
    dvdIsos = DvdIso.list()
    assert(dvdIsos.size > 0)
  }

  test("Get a DVD ISO") {
    var dvdIso = DvdIso.get(dvdIsos(0).id)
    assert(dvdIso.id == dvdIsos(0).id)
  }

}
