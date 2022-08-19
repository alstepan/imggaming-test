package imggaming.services.inmemory

import scala.concurrent.duration._
import org.scalatest.flatspec.AnyFlatSpec
import imggaming.model._
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class TestMatchStateImpl extends AnyFlatSpec {

  val vaildEvents = List(
    Event(15.seconds, 2, 0, Teams.Team1, 2),
    Event(30.seconds, 2, 3, Teams.Team2, 3),
    Event(38.minutes + 30.seconds, 100, 100, Teams.Team2, 3),
    Event(10.minutes + 10.seconds, 25, 20, Teams.Team1, 1),
    Event(22.minutes + 23.seconds, 48, 52, Teams.Team1, 2)
  )

  val incorrectScoreEvents = List(
    Event(15.seconds, 2, 0, Teams.Team1, 2),
    Event(30.seconds, 2, 3, Teams.Team2, 3),
    Event(38.minutes + 30.seconds, 100, 100, Teams.Team2, 3),
    Event(10.minutes + 10.seconds, 25, 20, Teams.Team1, 1),
    Event(22.minutes + 23.seconds, 50, 7, Teams.Team1, 2),
    Event(22.minutes + 23.seconds, 7, 50, Teams.Team1, 2)
  )

  val eventOrdering = (e1: Event, e2: Event) =>  e1.elapsedTime.gteq(e2.elapsedTime)

  "inmemory.MatchStateImpl.allEvents" should "return all valid events starting from last one" in {
    val state = vaildEvents.foldLeft(MatchStateImpl.apply)((s, e) => s.processEvent(e))

    state.allEvents shouldBe vaildEvents.sortWith(eventOrdering)
    state.journal shouldBe vaildEvents.reverse.map(Right(_))
  }

  "inmemory.MatchStateImpl.processEvent" should "ignore duplicate events" in {
    val state =  (vaildEvents.head :: vaildEvents).foldLeft(MatchStateImpl.apply)((s, e) => s.processEvent(e))

    state.allEvents shouldBe vaildEvents.sortWith(eventOrdering)
    state.journal should contain (Left(DuplicateEvent(vaildEvents.head, "Duplicate event")))
  }

  "inmemory.MatchStateImpl.processEvent" should "ignore events with incorrect score" in {
    val state = incorrectScoreEvents.foldLeft(MatchStateImpl.apply)((s, e) => s.processEvent(e))
    val ie1 = incorrectScoreEvents.reverse.head
    val ie2 = incorrectScoreEvents.reverse.tail.head
    state.allEvents shouldBe incorrectScoreEvents.filter(p => p != ie1 && p != ie2).sortWith(eventOrdering)
    state.journal should contain(Left(InvalidEvent(ie1, "Event with an incorrect score")))
    state.journal should contain(Left(InvalidEvent(ie2, "Event with an incorrect score")))
  }

  "inmemory.MatchStateImpl.lastEvents" should "return latest n events skipping earliest" in {
    val state = vaildEvents.foldLeft(MatchStateImpl.apply)((s, e) => s.processEvent(e))

    state.lastEvents(3) shouldBe vaildEvents.sortWith(eventOrdering).take(3)
  }
}
