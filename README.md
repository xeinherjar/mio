# Mio
A Discord bot to learn Scala apps and play TTRPGS

## Config
- src/main/resources/discord.conf
```
discord.secrets {
  ApplicationID = ""
  PublicKey     = ""
  ClientSecret  = ""
  Token         = ""
  BotPermission = 108468109260
}
```

## Running Locally
- Proxy webhooks: `ngrok http 9000 --domain=next-mammoth-beloved.ngrok-free.app`

## TODO
- add pekko-persistince with casandra (CQRS)
- add postgres (slick)?
- actor per member
- add endpoints for http AND gRPC
- add auth for non discord interactions