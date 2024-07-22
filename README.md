# Mio [Multiple In Out]
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
- liquibase or flyway setup
- actor per member
- add endpoints for http AND gRPC
- add auth for non discord interactions
  - OAuth - Google, Github, Discord
  - [https://www.oauth.com](https://www.oauth.com)
  - Credentials.Provided.verify(String) <- prevent timing attacks

## Directory Layout
- actors
- algebra: business logic
- api: http routes
- app: Main and other projects
- models: Internal models for project
- repos: Any external data sources (database, web services)
  - dto: objects for serde, used at the api and repo levels to move to/from internal classes, (json/proto serde)

