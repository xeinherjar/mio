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
- docker exec -it mio-pekko-cassandra-1 cqlsh
- select * from pekko.messages;

## TODO
- add pekko-persistince with casandra (CQRS)?
- add postgres (slick)?
- actor per member/game
- add endpoints for http AND gRPC
  - tapir? https://github.com/softwaremill/tapir
- add auth for non discord interactions
  - OAuth - Google, Github, Discord
  - [https://www.oauth.com](https://www.oauth.com)
  - https://www.jannikarndt.de/blog/2018/10/oauth2-akka-http/
  - https://www.youtube.com/watch?v=NZwnZhwVPrs (RockJVM, http4s + OAuth)
  - Credentials.Provided.verify(String) <- prevent timing attacks
- Game Hierarchy
  - Game has:
    - GameMaster, System, Players
  - Game can:
    - Create, Delete, Pause, Add/Remove Players, GM
  - Join/Create/Invite
  - Players add stats, other data - trait with per game impl?
  - GM add data
- UI
  - GM and Players to add/view things
  - Send messages to Discord from Bot with UI


## Directory Layout
- actors
- algebra: business logic
- api: http routes
- app: Main and other projects
- models: Internal models for project
- repos: Any external data sources (database, web services)
  - dto: objects for serde, used at the api and repo levels to move to/from internal classes, (json/proto serde)

