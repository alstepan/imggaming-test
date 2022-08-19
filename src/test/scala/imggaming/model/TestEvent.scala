package imggaming.model

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec

import scala.concurrent.duration.DurationInt

class TestEvent extends AnyPropSpec with TableDrivenPropertyChecks{

  val happyCases  = Table(
    ("Test", "Expected"),
    (0x781002, Event(15.seconds, 2, 0, Teams.Team1, 2)),
    (0xf0101f, Event(30.seconds, 2, 3, Teams.Team2, 3)),
    (0x1310c8a1, Event(10.minutes + 10.seconds, 25, 20, Teams.Team1, 1)),
    (0x29f981a2, Event(22.minutes + 23.seconds, 48, 52, Teams.Team1, 2)),
    (0x48332327, Event(38.minutes + 30.seconds, 100, 100, Teams.Team2, 3))
  )

  property("Event.fromInt should successfully convert happy cases") {
    forAll(happyCases) {
      (test: Int, expected: Event) => Event.fromInt(test) shouldEqual expected
    }
  }
}
