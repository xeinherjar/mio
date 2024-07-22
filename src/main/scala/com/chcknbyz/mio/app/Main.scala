package com.chcknbyz.mio.app

import scala.io.StdIn

import com.chcknbyz.mio.api.DiscordRoutes
import com.chcknbyz.mio.models.Configs.DiscordConfig
import com.chcknbyz.mio.models.Discord
import com.chcknbyz.mio.repos.Discord.LiveDiscord
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "mio-app")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val config: Config = ConfigFactory.load()

    val discordConfig = DiscordConfig(
      config.getString("discord.secrets.ApplicationID"),
      config.getString("discord.secrets.PublicKey"),
      config.getString("discord.secrets.ClientSecret"),
      config.getString("discord.secrets.Token"),
    )

    val route = new DiscordRoutes(discordConfig).route
    val discordRepo = LiveDiscord(discordConfig, Http())
    // discordRepo.installSlashCommands

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(
      s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...",
    )
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
