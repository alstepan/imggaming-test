package imggaming

import imggaming.model._
import imggaming.services.MatchState

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Try, Using}

object Application {

  def main(args: Array[String]): Unit = {
    processArgs(args.toList, MatchState.inMemory)
  }

  @tailrec
  def processArgs(args: List[String], matchState: MatchState): Unit = args match {
    case Nil => ()
    case "--streamFrom" :: file :: tail =>
      printHeader(s"Reading data from $file")
      processArgs(tail, streamFrom(file, matchState))
    case "--allEvents" :: tail =>
      allEvents(matchState)
      processArgs(tail, matchState)
    case "--lastEvent" :: tail =>
      lastEvent(matchState)
      processArgs(tail, matchState)
    case "--lastEvents" :: n :: tail if Try(Integer.parseInt(n)).isSuccess =>
      lastEvents(matchState, Integer.parseInt(n))
      processArgs(tail, matchState)
    case "--journal" :: tail =>
      journal(matchState)
      processArgs(tail, matchState)
    case arg => usage(arg.head)
  }

  def usage(arg: String): Unit =
    print(
      s"""
        |Unknown command line parameter $arg has been found
        |
        |Usage:
        |  java -jar imggaming-assembly-0.1.0.jar <commands>
        |
        |where commands are:
        |  --streamFrom <filename>     reads hexadecimal event data from file located at <filename> path.
        |  --allEvents                 prints all events stored before this command is executed
        |  --lastEvent                 prints the last event stored before this command is executed
        |  --lastEvents <num>          prints <num> last events before this command is executed
        |  --journal                   prints journal of events as they were processed
        |
        |Example:
        |   java -jar imggaming-assembly-0.1.0.jar --streamFrom sample1.txt --allEvents --journal
        |""".stripMargin)


  def streamFrom(file: String, state: MatchState = MatchState.inMemory): MatchState = {
    Using(Source.fromFile(file)){ buffer =>
      buffer.getLines().foldLeft(state){ (state, line) =>
        Try(Integer.parseInt(line.trim.replace("0x", ""), 16))
          .map(Event.fromInt)
          .map(state.processEvent)
          .fold(
            err => {
              println(s"ERROR: Cannot parse event $line in file $file due to $err. Ignoring...")
              state
            },
            st => st)
      }
    }.fold(
      err => {
        println(s"ERROR: Cannot open file $file due to $err")
        state
      },
      st => st
    )
  }

  def allEvents(state: MatchState): Unit = {
    printHeader("All match events:")
    state.allEvents.foreach(println)
  }

  def lastEvent(state: MatchState): Unit = {
    printHeader("Last match event:")
    state.lastEvents().foreach(println)
  }

  def lastEvents(state: MatchState, n: Int): Unit = {
    printHeader(s"Last $n events of the match:")
    state.lastEvents(n).foreach(println)
  }

  def journal(state: MatchState): Unit = {
    printHeader("Journal:")
    state.journal.foreach(println)
  }

  def printHeader(header: String): Unit = {
    println("-" * 10)
    println(header)
    println("-" * 10)
  }

}