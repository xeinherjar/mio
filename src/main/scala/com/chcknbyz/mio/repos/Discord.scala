package com.chcknbyz.mio.repos

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import cats.implicits._
import com.chcknbyz.mio.models.Configs.DiscordConfig
import com.chcknbyz.mio.models.Discord._
import com.chcknbyz.mio.repos.dto.DiscordJsonSupport.given
import com.github.pjfanning.pekkohttpcirce.ErrorAccumulatingCirceSupport._
import io.circe.syntax._
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl._
import org.apache.pekko.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, RequestEntity, headers, _}
import org.apache.pekko.http.scaladsl.unmarshalling._

trait DiscordRepo:
  def installSlashCommands: Unit

object Discord {
  class LiveDiscord(config: DiscordConfig, httpClient: HttpExt)(using ActorSystem[Any], ExecutionContext)
      extends DiscordRepo {
    override def installSlashCommands: Unit = {
      println("Calling Discord")
      val payload = ApplicationCommand(
        ApplicationCommandType.ChatInput.some,
        "mio",
        "Mio Bot commands".some,
        List(
          ApplicationCommandOption(
            ApplicationCommandOptionType.String,
            "roll",
            "roll some dice",
          ),
          ApplicationCommandOption(
            ApplicationCommandOptionType.String,
            "game",
            "game option",
          ),
        ).some,
        true,
      )

      val entity: RequestEntity = HttpEntity(ContentTypes.`application/json`, payload.asJson.noSpaces)

      val responseFuture: Future[HttpResponse] = Http().singleRequest(
        HttpRequest(
          method = HttpMethods.POST,
          uri = s"https://discord.com/api/v10/applications/${config.applicationId}/commands",
          headers = List(
            headers.RawHeader("Authorization", s"Bot ${config.token}"),
            headers.`User-Agent`("Mio Bot: Play Pekko-Http"),
            // "Content-Type"  -> "application/json; charset=UTF-8",
          ),
          entity = entity,
        ),
      )

      responseFuture
        .onComplete {
          case Success(_, _, entity, _) =>
            val tmp = Unmarshal(entity).to[String] // TODO: deal with future
            println(s"GOT: $tmp")
            entity.discardBytes()
          case Failure(_) => sys.error("something wrong")
        }
    }
  }
}
