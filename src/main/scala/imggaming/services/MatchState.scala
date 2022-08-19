package imggaming.services

import imggaming.model._
import imggaming.services.inmemory._

trait MatchState {
  def lastEvents(n: Int = 1): Seq[Event]
  def allEvents: Seq[Event]
  def processEvent(event: Event): MatchState
  def journal: Seq[Either[Error, Event]]
}

object MatchState {
  def inMemory: MatchState = MatchStateImpl.apply
}
