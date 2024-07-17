package com.chcknbyz.mio.api

import org.apache.pekko.http.scaladsl.server.Directives
import org.bouncycastle.crypto.Signer
import org.bouncycastle.crypto.params.{Ed25519PrivateKeyParameters, Ed25519PublicKeyParameters}
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.nio.charset.StandardCharsets
import com.chcknbyz.mio.models.Discord.Interaction
import com.chcknbyz.mio.models.Discord
import com.chcknbyz.mio.repos.dto.DiscordJsonSupport

import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model._

import com.chcknbyz.mio.models.Configs.DiscordConfig
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

class DiscordRoutes(val config: DiscordConfig) extends Directives with DiscordJsonSupport {
  val validateDiscordRequest = (headerValueByName("X-Signature-Ed25519") & headerValueByName(
    "X-Signature-Timestamp"
  ) & entity(as[String])).tflatMap[Unit] { case (signature, timestamp, body) =>
    def strToHex(s: String): Array[Byte] =
      s.sliding(2, 2).toArray.map(Integer.parseInt(_, 16).toByte)
    val key = strToHex(config.publicKey)
    val payload = (timestamp + body).getBytes(StandardCharsets.UTF_8)
    val verifier = new Ed25519Signer
    verifier.init(false, new Ed25519PublicKeyParameters(key, 0))
    verifier.update(payload, 0, payload.length)

    verifier.verifySignature(strToHex(signature)) match {
      case true  => pass
      case false => complete(StatusCodes.BadRequest)
    }

  }
  val route =
    pathPrefix("api" / "interactions") {
      concat(
        get(complete("Awake")),
        (post & validateDiscordRequest & entity(as[Discord.Interaction])) { interaction =>
          interaction.`type` match {
            case Discord.InteractionType.ApplicationCommand => {
              val data = interaction.data.map(_.convertTo[Discord.ApplicationCommandData])

              ???
            }
            case Discord.InteractionType.Ping =>
              complete(Discord.InteractionCallbackType.Pong)
            case Discord.InteractionType.ModalSubmit                    => ???
            case Discord.InteractionType.MessasgeComponent              => ???
            case Discord.InteractionType.ApplicationCommandAutocomplete => ???
          }
          // complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s""))
        }
      )
    }

}
