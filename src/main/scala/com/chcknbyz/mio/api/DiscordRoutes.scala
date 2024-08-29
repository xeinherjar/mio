package com.chcknbyz.mio.api

import java.nio.charset.StandardCharsets
import cats.implicits._
import cats.syntax.all.*
import com.chcknbyz.mio.actors.Discord as DiscordActor
import com.chcknbyz.mio.algebra.Dice
import com.chcknbyz.mio.models.Configs.DiscordConfig
import com.chcknbyz.mio.models.Discord
import com.chcknbyz.mio.models.Discord.Interaction
import com.chcknbyz.mio.repos.dto.DiscordJsonSupport.given
import com.github.pjfanning.pekkohttpcirce.ErrorAccumulatingCirceSupport.*
import io.circe.syntax.*
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.http.scaladsl.model.*
import org.apache.pekko.http.scaladsl.server.Directives
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.unmarshalling.PredefinedFromEntityUnmarshallers.*
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.AskPattern.*
import org.apache.pekko.util.Timeout
import scala.util.Failure
import scala.util.Success

class DiscordRoutes(val config: DiscordConfig, discordActor: ActorRef[DiscordActor.Command])(using
    system: ActorSystem[?],
    timeout: Timeout,
) extends Directives { // with ErrorAccumulatingCirceSupport ???
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
                        case "total" =>
                          complete(
                            /*
                             *onSuccess(
                             * discordActor.ask(replyTo => DiscordActor.GetTotal(replyTo))(
                             *  )
                             *)
                             */
                            ???
                          )
                        case "roll" =>
                          val diceRoll = head.value.flatMap(_.asString).getOrElse("")
                          val diceResult = Dice.parseRoll.parseAll(diceRoll).map(Dice.result)
                          complete(
                            diceResult.fold(
                              errors => HttpResponse(StatusCodes.BadRequest, entity = ""),
                              result =>
                                discordActor.tell(DiscordActor.AddRoll(result))
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
