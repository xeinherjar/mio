package com.chcknbyz.mio.app

import com.chcknbyz.mio.models.Configs.DiscordConfig
import com.chcknbyz.mio.repos.Discord.LiveDiscord
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http

object DiscordRegistration {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "mio-app")
    implicit val executionContext = system.executionContext

    val config: Config = ConfigFactory.load()

    val discordConfig = DiscordConfig(
      config.getString("discord.secrets.ApplicationID"),
      config.getString("discord.secrets.PublicKey"),
      config.getString("discord.secrets.ClientSecret"),
      config.getString("discord.secrets.Token"),
    )

    val discordRepo = LiveDiscord(discordConfig, Http())
    discordRepo.installSlashCommands

    println(s"Registraion complete")
  }
}
