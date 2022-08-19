package imggaming.model

sealed trait Error
case class InvalidEvent(origin: Event, details: String) extends Error
case class DuplicateEvent(origin: Event, details: String) extends Error