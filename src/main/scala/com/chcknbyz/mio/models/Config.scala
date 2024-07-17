package com.chcknbyz.mio.models

object Configs {
  case class DiscordConfig(
      applicationId: String,
      publicKey: String,
      clientSecret: String,
      token: String
  )
}
