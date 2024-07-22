package com.chcknbyz.mio.api

import java.nio.charset.StandardCharsets

import cats.syntax.all._
import com.chcknbyz.mio.models.Configs.DiscordConfig
import com.chcknbyz.mio.models.Discord.Interaction
import com.chcknbyz.mio.models.{Dice, Discord}
import com.chcknbyz.mio.repos.dto.DiscordJsonSupport.given
import com.github.pjfanning.pekkohttpcirce.ErrorAccumulatingCirceSupport._
import io.circe.syntax._
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.server.Directives
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.unmarshalling.PredefinedFromEntityUnmarshallers._
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer

class DiscordRoutes(val config: DiscordConfig) extends Directives {
  val validateDiscordRequest = (headerValueByName("X-Signature-Ed25519") & headerValueByName(
    "X-Signature-Timestamp",
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
      case false => complete(HttpResponse(StatusCodes.BadRequest, entity = "Bad Signature"))
    }

  }
  val route =
    pathPrefix("api" / "interactions") {
      concat(
        get(complete("Awake")),
        (post & validateDiscordRequest & entity(as[Discord.Interaction])) { interaction =>
          interaction.`type` match {
            case Discord.InteractionType.ApplicationCommand =>
              val data = interaction.data.map(_.as[Discord.ApplicationCommandData])
              data match
                case None => complete(HttpResponse(StatusCodes.BadRequest, entity = "Missing Interaction Data"))
                case Some(Left(err)) => complete(HttpResponse(StatusCodes.BadRequest, entity = s"DECODE: $err"))
                case Some(Right(acd)) =>
                  acd.options match
                    case None => complete(HttpResponse(StatusCodes.BadRequest, entity = "Missing Command"))
                    case l @ Some(head :: tail) if !l.isEmpty =>
                      head.name match
                        // TODO: Enumerate commands, move to Algebra
                        case "roll" =>
                          val diceRoll = head.value.flatMap(_.asString).getOrElse("")
                          val diceResult = Dice.parseRoll.parseAll(diceRoll).map(Dice.result)
                          complete(
                            diceResult.fold(
                              errors => HttpResponse(StatusCodes.BadRequest, entity = ""),
                              result =>
                                Discord
                                  .InteractionResponse(
                                    Discord.InteractionCallbackType.ChannelMessageWithSource,
                                    Discord
                                      .InteractionResponseData(
                                        result.toString.some,
                                      )
                                      .some,
                                  )
                                  .asJson,
                            ),
                          )

                        case _ => complete(HttpResponse(StatusCodes.BadRequest, entity = "Not a valid command"))
                    case _ => complete(HttpResponse(StatusCodes.BadRequest, entity = "Not a valid command"))

            case Discord.InteractionType.Ping =>
              complete(
                Discord.InteractionCallbackType.Pong.asJson,
              )
            case Discord.InteractionType.ModalSubmit                    => ???
            case Discord.InteractionType.MessasgeComponent              => ???
            case Discord.InteractionType.ApplicationCommandAutocomplete => ???
          }
          // complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s""))
        },
      )
    }

}
