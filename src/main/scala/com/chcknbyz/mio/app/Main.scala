package com.chcknbyz.mio.app

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.io.StdIn
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config

import org.bouncycastle.crypto.Signer
import org.bouncycastle.crypto.params.{Ed25519PrivateKeyParameters, Ed25519PublicKeyParameters}
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.nio.charset.StandardCharsets
import com.chcknbyz.mio.models.Discord.Interaction
import com.chcknbyz.mio.models.Discord
import com.chcknbyz.mio.models.Configs.DiscordConfig
import com.chcknbyz.mio.api.DiscordRoutes

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
      config.getString("discord.secrets.Token")
    )

    val route = new DiscordRoutes(discordConfig).route

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(
      s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop..."
    )
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
