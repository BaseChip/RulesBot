# env_RulesBot
> Create server rules new users need to confirm first in order to access the server
<br>
<a href="https://discordbots.org/bot/389082834670845952" >
  <img src="https://discordbots.org/api/widget/servers/389082834670845952.svg" alt="Server count" />
</a>
<a href="https://discordbots.org/bot/389082834670845952" >
  <img src="https://discordbots.org/api/widget/status/389082834670845952.svg" alt="Status" />
</a>
<a href="https://status.thebotdev.de">
  <img src="https://img.shields.io/badge/Status-https://status.thebotdev.de-blue.svg">
</a>

The Rules Bot helps you managing new members by providing an automated verification system using Discord's message reactions.

<img src="https://thebotdev.de/img/bot_img.png" alt="Logo" width=200>
<a href="https://discord.gg/HD7x2vx">
    <img src="https://canary.discordapp.com/api/guilds/385848724628439062/widget.png?style=banner2" >
</a>

## You need

OS X & Linux:

You need:
```sh
https://github.com/Rapptz/discord.py/archive/rewrite.zip
aiohttp
peewee
flask
requests_oauthlib
requests
https://github.com/romangraef/configlib/archive/master.zip
```
## Usage example

With RulesBot you can create a rule message to ensure that all your users accept your rules to use your server. Furthermore, this bot can add several reactions to your message, which automatically assigns a certain role to the users.

_You can also visit our [website][wiki]._

## Development setup

**Step 1**: Make sure, you have the [requirment packages](requirements.txt) installed (pip install -r requirements.txt)

**Step 2:** Clone this repository with git clone `https://github.com/BaseChip/RulesBot`

**Step 3:** Insert your Token into your config (create a folder called config and a file there called config.json)

**Step 4:** run the bot with python3.6 main.py

## Release History
<img src="https://img.shields.io/badge/Version-2.2-green.svg" alt="version">

* v2.2
    + NEW: ticket system (create_ticket)
    + NEW: premium (premium)
    + NEW: added ping to statuspage (status.thebotdev.de)
    + NEW: remove a role on setup (available now)
    + NEW: custom embed footer (just for premium members)
    + NEW: custom embed color (just for premium members)
    + BUGFIX: Bot should be much more stable now
     
* v2.1
    * NEW: extra Bot file for an bot who can test if the bot is online
    * NEW: mention in the log messages
    * CHANGE: some bugfixes
    * CHANGE: time out time now 120 sec.
    * CHANGE: better join message
    
* v2
    * NEW: emojis under a message to get a role
    * NEW: recovery command (-restore)
    * NEW: this README
    * CHANGE: Complete Rewrite
    * CHANGE: Now channel mention instead of the channel id
    * CHANGE: better ping message
    * CHANGE: more error checks during the setup
    * CHANGE: eval command for the bot owner(s)
    * CHANGE: new texts
    * BUG FIX: if you have too many roles the setup will now also work
    * BUG FIX: thee bot test now his permissions during the setup
    * BUG FIX: when reaction is removed -> role will be removed 
    * REMOVE: Time part in the setup


## Meta

Bot by: BaseChip#2390 // romangraef89#0998 // Skidder#0001
<br>
##### Support server: https://discord.gg/HD7x2vx

## Contributing

1. Fork it (<https://github.com/basechip/RulesBot/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Commit your changes (`git commit -am 'Add some fooBar'`)
4. Push to the branch (`git push origin feature/fooBar`)
5. Create a new Pull Request

## Self-hosting Agreement
* You may not use the Rules Bot logo or name within derivative bots.
* You may not host a public version of Rules Bot.
* You may not charge for the usage of your instance of Rules Bot.
* You may not provide support for Rules Bot.
## Sponsoring
**Partner**<br>
https://dyn-box.de
<br>
<!-- Markdown link & img dfn's -->
[npm-image]: https://img.shields.io/npm/v/datadog-metrics.svg?style=flat-square
[npm-url]: https://npmjs.org/package/datadog-metrics
[npm-downloads]: https://img.shields.io/npm/dm/datadog-metrics.svg?style=flat-square
[travis-image]: https://img.shields.io/travis/dbader/node-datadog-metrics/master.svg
[travis-url]: https://thebotdev.de
[wiki]: https://docs.thebotdev.de
