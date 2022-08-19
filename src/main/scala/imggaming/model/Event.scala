package imggaming.model

import scala.concurrent.duration._

case class Event(elapsedTime: Duration, team1Total: Int, team2Total: Int, whoScored: Teams.Team, pointsScored: Byte) {
  def elapsedTimeToString: String =
    f"${elapsedTime.toHours}%02d:${elapsedTime.toMinutes % 60}%02d:${elapsedTime.toSeconds % 60}%02d"
  override def toString: String =
    s"$elapsedTimeToString --> Team 1: $team1Total, Team 2: $team2Total,  Scored: $whoScored, Points: $pointsScored"
}


object Event {

  def fromInt(v: Int): Event =
    Event(
      pointsScored = (v & 3).toByte,
      whoScored = if (((v >> 2) & 1) == 0) Teams.Team1 else Teams.Team2,
      team2Total = (v >> 3) & 255,
      team1Total = (v >> 11) & 255,
      elapsedTime = ((v >> 19) & 4095).seconds
    )
}