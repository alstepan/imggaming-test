package imggaming.services.inmemory

import imggaming.model._
import imggaming.services.MatchState

import scala.collection.SortedSet

object MatchStateImpl {
  implicit val eventOrdering: Ordering[Event] = (x: Event, y: Event) => y.elapsedTime.compare(x.elapsedTime)

  def apply: MatchState = MatchStateImpl()

  final case class MatchStateImpl(state: SortedSet[Event] = SortedSet(),
                                  eventJournal: List[Either[Error, Event]] = List()) extends MatchState {

    override def lastEvents(n: Int): Seq[Event] = state.take(n).toSeq

    override def allEvents: Seq[Event] = state.toSeq

    override def processEvent(event: Event): MatchState =
      checkDuplicate(event)
        .flatMap(checkScore(_, team1Score))
        .flatMap(checkScore(_, team2Score))
        .fold(
          error => MatchStateImpl(state, Left(error) :: eventJournal),
          event => MatchStateImpl(state ++ SortedSet(event), Right(event) :: eventJournal)
        )

    override def journal: Seq[Either[Error, Event]] = eventJournal

    private def checkDuplicate(e: Event): Either[Error, Event] =
      Either.cond(!state.contains(e), e, DuplicateEvent(e, "Duplicate event"))

    private val team1Score: Event => Int = _.team1Total
    private val team2Score: Event => Int = _.team2Total

    private def checkScore(e: Event, score: Event => Int): Either[Error, Event] =
      Either.cond(
        e.pointsScored != 0 &&
        state.minAfter(e).forall(ee => score(e) >= score(ee)) &&
        state.maxBefore(e).forall(ee => score(e) <= score(ee)),
        e,
        InvalidEvent(e, "Event with an incorrect score")
      )
  }
}


