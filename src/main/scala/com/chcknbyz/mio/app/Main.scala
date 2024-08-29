package com.chcknbyz.mio.app

import com.chcknbyz.mio.api.DiscordRoutes
import com.chcknbyz.mio.models.Configs.DiscordConfig
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.scaladsl.AskPattern.*
import org.apache.pekko.http.scaladsl.Http
import com.chcknbyz.mio.actors.Discord
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext

object Main {
  def start(config: DiscordConfig, actor: ActorRef[Discord.Command])(using system: ActorSystem[?], timeout: Timeout) = {
    given ExecutionContext = system.executionContext
    val route = new DiscordRoutes(config, actor).route
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    bindingFuture.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(s"Server online at http://${address.getHostString}:${address.getPort}")
      case Failure(ex) =>
        system.log.error(s"Failed to bind HTTP server, because: $ex")
        system.terminate()
    }
  }
  def main(args: Array[String]): Unit = {
    val config: Config = ConfigFactory.load()

    val discordConfig = DiscordConfig(
      config.getString("discord.secrets.ApplicationID"),
      config.getString("discord.secrets.PublicKey"),
      config.getString("discord.secrets.ClientSecret"),
      config.getString("discord.secrets.Token"),
    )

    trait RootCommand
    case class RetrieveDiscordActor(replyTo: ActorRef[ActorRef[Discord.Command]]) extends RootCommand

    val rootBehavior: Behavior[RootCommand] = Behaviors.setup { context =>
      context.log.info("Starting root Actor")
      val discordActor = context.spawn(Discord(), "Discord")
      Behaviors.receiveMessage { case RetrieveDiscordActor(replyTo) =>
        context.log.info(s"Got a message: $discordActor")
        replyTo ! discordActor
        Behaviors.same
      }
    }

    implicit val system = ActorSystem(rootBehavior, "mio-app")
    implicit val executionContext = system.executionContext
    implicit val timeout: Timeout = Timeout(5.seconds)

    val da: Future[ActorRef[Discord.Command]] = system
      .ask(replyTo => RetrieveDiscordActor(replyTo))
    da.foreach(start(discordConfig, _))
  }
}
