package com.chcknbyz.mio.repos.dto

import com.chcknbyz.mio.models.Discord._
import io.circe._
import io.circe.derivation.Configuration
import io.circe.generic.semiauto._

// TODO: how to return errors?
object DiscordJsonSupport {

  given Configuration = Configuration.default.withSnakeCaseMemberNames

  given Decoder[ApplicationCommand] = deriveDecoder
  given Encoder[ApplicationCommand] = deriveEncoder

  given Decoder[ApplicationCommandData] = deriveDecoder
  given Encoder[ApplicationCommandData] = deriveEncoder

  given Decoder[ApplicationCommandOption] = deriveDecoder
  given Encoder[ApplicationCommandOption] = deriveEncoder

  given Decoder[CommandOptionChoice] = deriveDecoder
  given Encoder[CommandOptionChoice] = deriveEncoder

  given Decoder[Interaction] = deriveDecoder
  given Encoder[Interaction] = deriveEncoder

  given Decoder[ApplicationCommandInteractionDataOption] = deriveDecoder
  given Encoder[ApplicationCommandInteractionDataOption] = deriveEncoder

  given Decoder[InteractionResponseData] = deriveDecoder
  given Encoder[InteractionResponseData] = deriveEncoder
  given Decoder[InteractionResponse] = deriveDecoder
  given Encoder[InteractionResponse] = deriveEncoder

  given Decoder[InteractionType] =
    (c: HCursor) =>
      for {
        n <- c.as[Int]
      } yield n match
        case 1 => InteractionType.Ping
        case 2 => InteractionType.ApplicationCommand
        case 3 => InteractionType.MessasgeComponent
        case 4 => InteractionType.ApplicationCommandAutocomplete
        case 5 => InteractionType.ModalSubmit

  given Encoder[InteractionType] = v =>
    v match
      case InteractionType.Ping                           => Json.fromInt(1)
      case InteractionType.ApplicationCommand             => Json.fromInt(2)
      case InteractionType.MessasgeComponent              => Json.fromInt(3)
      case InteractionType.ApplicationCommandAutocomplete => Json.fromInt(4)
      case InteractionType.ModalSubmit                    => Json.fromInt(5)

  given Decoder[ApplicationCommandType] =
    (c: HCursor) =>
      for {
        n <- c.as[Int]
      } yield n match
        case 1 => ApplicationCommandType.ChatInput
        case 2 => ApplicationCommandType.User
        case 3 => ApplicationCommandType.Message

  given Encoder[ApplicationCommandType] = v =>
    v match
      case ApplicationCommandType.ChatInput => Json.fromInt(1)
      case ApplicationCommandType.User      => Json.fromInt(2)
      case ApplicationCommandType.Message   => Json.fromInt(3)

  given Decoder[ApplicationCommandOptionType] =
    (c: HCursor) =>
      for {
        n <- c.as[Int]
      } yield n match
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

  given Encoder[ApplicationCommandOptionType] = v =>
    v match
      case ApplicationCommandOptionType.SubCommand      => Json.fromInt(1)
      case ApplicationCommandOptionType.SubCommandGroup => Json.fromInt(2)
      case ApplicationCommandOptionType.String          => Json.fromInt(3)
      case ApplicationCommandOptionType.Integer         => Json.fromInt(4)
      case ApplicationCommandOptionType.Boolean         => Json.fromInt(5)
      case ApplicationCommandOptionType.User            => Json.fromInt(6)
      case ApplicationCommandOptionType.Channel         => Json.fromInt(7)
      case ApplicationCommandOptionType.Role            => Json.fromInt(8)
      case ApplicationCommandOptionType.Mentionable     => Json.fromInt(9)
      case ApplicationCommandOptionType.Number          => Json.fromInt(10)
      case ApplicationCommandOptionType.Attachment      => Json.fromInt(11)

  given Decoder[InteractionCallbackType] =
    (c: HCursor) =>
      for {
        n <- c.as[Int]
      } yield n match
        case 1  => InteractionCallbackType.Pong
        case 4  => InteractionCallbackType.ChannelMessageWithSource
        case 5  => InteractionCallbackType.DeferredChannelMessageWithSource
        case 6  => InteractionCallbackType.DeferredUpdateMessage
        case 7  => InteractionCallbackType.UpdateMessage
        case 8  => InteractionCallbackType.ApplicationCommandAutocompleteResult
        case 9  => InteractionCallbackType.Modal
        case 10 => InteractionCallbackType.PremiumRequired

  given Encoder[InteractionCallbackType] = v =>
    v match
      case InteractionCallbackType.Pong                                 => Json.fromInt(1)
      case InteractionCallbackType.ChannelMessageWithSource             => Json.fromInt(4)
      case InteractionCallbackType.DeferredChannelMessageWithSource     => Json.fromInt(5)
      case InteractionCallbackType.DeferredUpdateMessage                => Json.fromInt(6)
      case InteractionCallbackType.UpdateMessage                        => Json.fromInt(7)
      case InteractionCallbackType.ApplicationCommandAutocompleteResult => Json.fromInt(8)
      case InteractionCallbackType.Modal                                => Json.fromInt(9)
      case InteractionCallbackType.PremiumRequired                      => Json.fromInt(10)
}
