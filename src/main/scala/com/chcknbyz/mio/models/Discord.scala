package com.chcknbyz.mio.models

import spray.json._

object Discord {
  type Snowflake = String

  enum ApplicationCommandType:
    case ChatInput, User, Message

  // https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-type
  // Number == Double
  enum ApplicationCommandOptionType:
    case SubCommand, SubCommandGroup, String, Integer, Boolean, User, Channel, Role, Mentionable,
      Number, Attachment

  // https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-interaction-callback-type
  enum InteractionCallbackType:
    case Pong, ChannelMessageWithSource, DeferredChannelMessageWithSource, DeferredUpdateMessage,
      UpdateMessage, ApplicationCommandAutocompleteResult, Modal, PremiumRequired

  // https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-type
  enum InteractionType:
    case Ping, ApplicationCommand, MessasgeComponent, ApplicationCommandAutocomplete, ModalSubmit

  // Create a slash command, for creating we only need a few fields
  // https://discord.com/developers/docs/interactions/application-commands#application-command-object
  case class ApplicationCommand(
      `type`: Option[ApplicationCommandType], // Defaults to ChatInput
      name: String,
      description: Option[String],
      options: Option[List[ApplicationCommandOption]],
      dm_permission: Boolean,
  )

  // Data differs depending on type, PING is nothing
  case class ApplicationCommandData(
    id: String,
    name: String,
    `type`: Int, // TODO enum
    // resolved: Option....
    options: Option[List[ApplicationCommandInteractionDataOption]],
    guildId: Option[String],
    targetId: Option[String]
  )

  // https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-application-command-interaction-data-option-structure
  // value and options are mutually exclusive, maybe I should use a trait?
  case class ApplicationCommandInteractionDataOption(
    name: String,
    `type`: ApplicationCommandOptionType,
    value: Option[JsValue], // String, Integer, Double, Boolean
    options: Option[List[ApplicationCommandInteractionDataOption]],
    focused: Option[Boolean]
  )

  // https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-structure
  case class ApplicationCommandOption(
      `type`: ApplicationCommandOptionType,
      name: String,
      description: String,
      choices: Option[List[CommandOptionChoice]] = None, // STRING, INTEGER, NUMBER
      options: Option[List[ApplicationCommandOption]] = None, // If subcommand, these are the params
      required: Boolean = false
  )

  // https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-choice-structure
  // value: string, integer, double
  // string max length of 100
  case class CommandOptionChoice(name: String, value: JsValue)

  // https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-data
  case class Interaction(
      id: Snowflake,
      applicationId: Snowflake,
      `type`: InteractionType,
      data: Option[JsValue], // Todo: Option[InteractionData],
      guildId: Option[Snowflake],
      // channel?
      channelId: Option[Snowflake],
      // member?
      // user?
      token: String,
      version: Int,
      message: Option[JsValue],
      appPermissions: Option[String]
      // local?
      // guildLocale
      // entitlements
      // authorizingIntergrationOwners
      // context?
  )

  // https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-interaction-response-structure
  case class InteractionResponse(
      `type`: InteractionCallbackType,
      data: Option[InteractionResponseData] = None
  )

  // https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-interaction-callback-data-structure
  case class InteractionResponseData(
      content: Option[String],
      // embeds?
      // allowedMentions?
      // flags?
      // components?
      // attachments?
      tts: Option[Boolean] = Some(false),
  )

}
