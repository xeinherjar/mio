package com.chcknbyz.mio.actors

import org.apache.pekko
import org.apache.pekko.actor.typed.{ActorRef, Behavior}

import pekko.persistence.typed.scaladsl.EventSourcedBehavior
import pekko.persistence.typed.scaladsl.Effect
import pekko.persistence.typed.PersistenceId

// actor stuffs

object Discord {
  // case class join(id: String, replyTo: ActorRef[Response]) extends Command
  // events --  to
  // state
  // case class state(games: Map[String, ActorRef[])
  // sealed trait Response
  sealed trait Command
  case class AddRoll(n: Int) extends Command
  case class GetTotal(replyTo: ActorRef[Response]) extends Command

  sealed trait Event
  case class UpdateState(n: Int) extends Event

  sealed trait Response
  case class RollTotal(n: Int) extends Response

  final case class State(n: Int)

  def apply(): Behavior[Command] =
    EventSourcedBehavior[Command, Event, State](
      persistenceId = PersistenceId.ofUniqueId("Discord"),
      emptyState = State(0),
      commandHandler = (state, cmd) => {
        cmd match
          case AddRoll(n) => Effect.persist(UpdateState(n))
          case GetTotal(replyTo) => Effect.reply(replyTo)(RollTotal(state.n))
      },
      eventHandler = (state, evt) => {
        evt match
          case UpdateState(n) => State(state.n + n)
      },
    )

}
