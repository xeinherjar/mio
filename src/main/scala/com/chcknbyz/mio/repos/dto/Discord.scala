package com.chcknbyz.mio.repos.dto

import spray.json._
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import com.chcknbyz.mio.models.Discord.*

trait DiscordJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  given StringJsonFormat.type = StringJsonFormat

  given ApplicationCommandTypeFormat: RootJsonFormat[ApplicationCommandType] =
    new RootJsonFormat[ApplicationCommandType] {
      // implicit object ApplicationCommandTypeFormat extends RootJsonFormat[ApplicationCommandType] {
      def read(json: JsValue): ApplicationCommandType = json match
        case JsNumber(value) =>
          value.toInt match {
            case 1 => ApplicationCommandType.ChatInput
            case 2 => ApplicationCommandType.User
            case 3 => ApplicationCommandType.Message
            case n => deserializationError("Expected (1-3), recieved: $n")
          }
        case otherwise => deserializationError("Expected Int, recieved: $otherwise ")

      def write(obj: ApplicationCommandType): JsValue = obj match
        case ApplicationCommandType.ChatInput => JsNumber(1)
        case ApplicationCommandType.User      => JsNumber(2)
        case ApplicationCommandType.Message   => JsNumber(3)
    }

  given ApplicationCommandOptionTypeFormat: RootJsonFormat[ApplicationCommandOptionType] =
    new RootJsonFormat[ApplicationCommandOptionType] {
      def read(json: JsValue): ApplicationCommandOptionType = json match
        case JsNumber(value) =>
          value.toInt match
            case 1  => ApplicationCommandOptionType.SubCommand
            case 2  => ApplicationCommandOptionType.SubCommandGroup
            case 3  => ApplicationCommandOptionType.String
            case 4  => ApplicationCommandOptionType.Integer
            case 5  => ApplicationCommandOptionType.Boolean
            case 6  => ApplicationCommandOptionType.User
            case 7  => ApplicationCommandOptionType.Channel
            case 8  => ApplicationCommandOptionType.Role
            case 9  => ApplicationCommandOptionType.Mentionable
            case 10 => ApplicationCommandOptionType.Number
            case 11 => ApplicationCommandOptionType.Attachment
            case n  => deserializationError("Expected (1-11), received: $n")
        case otherwise => deserializationError("Expected Int, recieved: $otherwise ")

      def write(obj: ApplicationCommandOptionType): JsValue = obj match
        case ApplicationCommandOptionType.SubCommand      => JsNumber(1)
        case ApplicationCommandOptionType.SubCommandGroup => JsNumber(2)
        case ApplicationCommandOptionType.String          => JsNumber(3)
        case ApplicationCommandOptionType.Integer         => JsNumber(4)
        case ApplicationCommandOptionType.Boolean         => JsNumber(5)
        case ApplicationCommandOptionType.User            => JsNumber(6)
        case ApplicationCommandOptionType.Channel         => JsNumber(7)
        case ApplicationCommandOptionType.Role            => JsNumber(8)
        case ApplicationCommandOptionType.Mentionable     => JsNumber(9)
        case ApplicationCommandOptionType.Number          => JsNumber(10)
        case ApplicationCommandOptionType.Attachment      => JsNumber(11)
    }

  implicit object InteractionTypeFormat extends RootJsonFormat[InteractionType] {
    def read(json: JsValue): InteractionType =
      json match {
        case JsNumber(number) =>
          number.toInt match {
            case 1         => InteractionType.Ping
            case 2         => InteractionType.ApplicationCommand
            case 3         => InteractionType.MessasgeComponent
            case 4         => InteractionType.ApplicationCommandAutocomplete
            case 5         => InteractionType.ModalSubmit
            case otherwise => deserializationError("Expected (1-5), recieved: $otherwise ")
          }
        case otherwise => deserializationError("Expected Int, recieved: $otherwise ")
      }

    def write(obj: InteractionType): JsValue =
      obj match {
        case InteractionType.Ping                           => JsNumber(1)
        case InteractionType.ApplicationCommand             => JsNumber(2)
        case InteractionType.MessasgeComponent              => JsNumber(3)
        case InteractionType.ApplicationCommandAutocomplete => JsNumber(4)
        case InteractionType.ModalSubmit                    => JsNumber(5)
      }
  }



  given applicationCommandInteractionDataOptionFormat: RootJsonFormat[ApplicationCommandInteractionDataOption] = jsonFormat(
    ApplicationCommandInteractionDataOption.apply,
    "name",
    "type",
    "value",
    "options",
    "focused"
  )

  given applicationCommandDataFormat: RootJsonFormat[ApplicationCommandData] = jsonFormat(
    ApplicationCommandData.apply,
    "id",
    "name",
    "type",
    "options",
    "guildId",
    "targetId"
  )

  given interactionFormat: RootJsonFormat[Interaction] = jsonFormat(
    Interaction.apply,
    "id",
    "application_id",
    "type",
    "data",
    "guild_id",
    "channel_id",
    "token",
    "version",
    "message",
    "app_permissions"
  )

  implicit object InteractionCallbackTypeFormat extends RootJsonFormat[InteractionCallbackType] {
    def read(json: JsValue): InteractionCallbackType =
      json match {
        case JsNumber(number) =>
          number.toInt match {
            case 1         => InteractionCallbackType.Pong
            case 4         => InteractionCallbackType.ChannelMessageWithSource
            case 5         => InteractionCallbackType.DeferredChannelMessageWithSource
            case 6         => InteractionCallbackType.DeferredUpdateMessage
            case 7         => InteractionCallbackType.UpdateMessage
            case 8         => InteractionCallbackType.ApplicationCommandAutocompleteResult
            case 9         => InteractionCallbackType.Modal
            case 10        => InteractionCallbackType.PremiumRequired
            case otherwise => deserializationError("Expected (1 or 4-10), recieved: $otherwise")
          }
        case otherwise => deserializationError("Expected Int, recieved: $otherwise")
      }

    def write(obj: InteractionCallbackType): JsValue =
      obj match {
        case InteractionCallbackType.Pong                                 => JsNumber(1)
        case InteractionCallbackType.ChannelMessageWithSource             => JsNumber(4)
        case InteractionCallbackType.DeferredChannelMessageWithSource     => JsNumber(5)
        case InteractionCallbackType.DeferredUpdateMessage                => JsNumber(6)
        case InteractionCallbackType.UpdateMessage                        => JsNumber(7)
        case InteractionCallbackType.ApplicationCommandAutocompleteResult => JsNumber(8)
        case InteractionCallbackType.Modal                                => JsNumber(9)
        case InteractionCallbackType.PremiumRequired                      => JsNumber(10)
      }
  }
}
