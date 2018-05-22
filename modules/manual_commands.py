import datetime
import time

import aiohttp
import discord
from discord import Embed, Color, Message, TextChannel
from discord import Webhook, AsyncWebhookAdapter
from discord.ext import commands
from discord.raw_models import RawReactionActionEvent

import KEYS
from database import *
from edit_message_handler import handle_editmessage
from help_handler import handle_help
from info_handler import handle_info

conn = db
cur = db.cursor()


class ManualCog(object):

    def __init__(self, bot: commands.Bot):
        self.bot: commands.Bot = bot

    async def error(self, code, message):
        await message.channel.send(
            embed=Embed(
                color=Color.red,
                description="An error has occurred with the following error code - %s - If you cannot solve the problem, "
                            "please join the support server https://discord.gg/HD7x2vx" % code))

    async def on_ready(self):
        db.create_tables([ServerData, ServerSettings, UserData, ReactionAction])
        game = discord.Game(name="-help | Sponsored by dyn-box.de")
        await self.bot.change_presence(status=discord.Status.online, activity=game)
        print("Login erfolgreich!")
        print(len(self.bot.guilds))

    async def on_message(self, message):
        guild = message.guild
        print(guild.id)
        server_data: ServerData = ServerData.select().where(ServerData.serverid == guild.id).get()
        server_settings: ServerSettings = ServerSettings.select().where(ServerSettings.gid == guild).get()
        prefix = server_settings.prefix
        inleng = len(prefix)

        if message.content.startswith(prefix):
            invoke = message.content[inleng:].split(" ")[0]
            args = message.content.split(" ")[1:]
            if invoke == "botinfo" and message.author.id == 333220752117596160:
                embed = discord.Embed(color=0x80ffff)
                embed.add_field(name="Guilds", value=str(len(self.bot.guilds)), inline=False)
                embed.add_field(name="User", value=str(len(self.bot.users)))
                await message.channel.send(embed=embed)
            if invoke == "info":
                await handle_info(message=message)
            if invoke == "invite":
                embed = discord.Embed(title="Click to invite",
                                      url="https://discordapp.com/oauth2/authorize?client_id=389082834670845952"
                                          "&permissions=268634182&scope=bot",
                                      color=0x27fcfc)
                embed.set_author(name="Recommend me to others ")
                await message.channel.send(embed=embed)
            if invoke == "supporter":
                await message.channel.send(
                    "Here is a list of all generous donors who have supported the bot here:\nsolana Dracunculas#0297")
            if invoke == "help":
                await handle_help(message=message, server_settings=server_settings)
            if invoke == "editmessage":
                if message.author.guild_permissions.administrator or message.author.id == 333220752117596160:
                    await handle_editmessage(message=message, client=self.bot, server_data=server_data)
            if invoke == "editmessage" and not message.author.guild_permissions.administrator:
                await message.channel.send(
                    embed=discord.Embed(
                        color=discord.Color.red(),
                        description="This command can only use administrators, sorry"
                    ).set_thumbnail(
                        url="https://thebotdev.de/assets/img/alert.png"))

    async def on_raw_reaction_add(self, event: RawReactionActionEvent):
        message_id = event.message_id
        channel_id = event.channel_id
        user_id = event.user_id
        emoji = event.emoji
        gu = self.bot.get_channel(channel_id)
        guild = gu.guild.id
        channel: TextChannel = self.bot.get_channel(channel_id)
        message: Message = await channel.get_message(message_id)
        gugui = self.bot.get_guild(guild)
        user: discord.Member = gugui.get_member(user_id)
        if user.bot:
            return
        server_data: ServerData = ServerData.select().where(ServerData.serverid == guild).get()
        ruleschannel = server_data.messageid
        username = user.name
        userid = user.id
        roleid = server_data.roleid
        role = discord.utils.get(gugui.roles, id=roleid)
        user_data: UserData = UserData.get_or_insert(user)
        if message_id == ruleschannel:
            if str(emoji) == "✅":
                await user.add_roles(role, reason="accepted the rules", atomic=True)
                await self.logmsgaccepted(user, username, guild)
                user_data.status = 'accepted'
                user_data.save()
            elif str(emoji) == "❌":
                await message.remove_reaction("❌", user)
                action = server_data.action
                if action == "kick":
                    unix = int(time.time())
                    now = str(datetime.datetime.fromtimestamp(unix).strftime('%H:%M'))
                    await self.logmsg(user, username, now, "kick", guild)
                    if user.name != "Rules Bot":
                        sisam = server_data.kick
                        if sisam == "Yes":
                            msgtext = server_data.kickmsg
                            await user.send(
                                embed=discord.Embed(color=discord.Color.red(), description=msgtext))
                        await user.kick()
                        user_data.status = "kick"
                        user_data.save()
                elif action == "ban":
                    unix = int(time.time())
                    now = str(datetime.datetime.fromtimestamp(unix).strftime('%H:%M'))
                    user = self.bot.get_user(user_id)
                    username = user.name
                    a = "ban"
                    await self.logmsg(user, username, now, a, guild)
                    if user.name != "Rules Bot":
                        sisam = server_data.kick
                        if sisam == "Yes":
                            msgtext = server_data.kickmsg
                            await user.send(
                                embed=discord.Embed(color=discord.Color.red(), description=msgtext))
                        await user.ban(reason="Declined the rules")
                        user_data.status = "banned"
                        user_data.save()
            db.commit()

    async def on_raw_reaction_remove(self, event: RawReactionActionEvent):
        message_id = event.message_id
        channel_id = event.channel_id
        user_id = event.user_id
        emoji = event.emoji
        gu = self.bot.get_channel(channel_id)
        guild = gu.guild.id
        gugui = self.bot.get_guild(guild)
        user: discord.Member = gugui.get_member(user_id)
        server_data: ServerData = ServerData.select().where(ServerData.serverid == guild).get()
        ruleschannel = server_data.messageid
        username = user.name
        userid = user.id
        roleid = server_data.roleid
        role = discord.utils.get(gugui.roles, id=roleid)
        user_data: UserData = UserData.get_or_insert(user)
        if message_id == ruleschannel:
            if str(emoji) == "✅":
                await user.remove_roles(role, reason="removed reaction", atomic=True)
                unix = int(time.time())
                now = str(datetime.datetime.fromtimestamp(unix).strftime('%H:%M'))
                await self.logmsg(user, username, now, "removed role", guild)
                user_data.status = 'removed'
                user_data.save()
            db.commit()

    async def on_guild_join(self, guild):
        server_settings: ServerSettings = ServerSettings.get_or_insert(guild)
        server_data: ServerData = ServerData.get_or_insert(guild)
        db.commit()
        await guild.owner.send(content="Hey thanks for adding me to your server!\n"
                                       "If you wont get started please type in on your guild `-help` or you can change"
                                       " my prefix with `-pchange`")

    async def on_member_join(self, member):
        usrid = member.id
        usrname = member.name
        unix = int(time.time())
        guild = member.guild
        status = "undefined"
        jointime = str(datetime.datetime.fromtimestamp(unix).strftime('%H:%M'))
        user_data: UserData = UserData.get_or_insert(member)
        user_data.save()
        server_data: ServerData = ServerData.get_or_insert(guild)
        if server_data.shjoin == "Yes":
            await member.send(embed=discord.Embed(color=0x27fcfc, description=server_data.joinmsg))

    async def logmsg(self, usr, usrname, time, action, guild):
        if usrname != "Rules Bot":  # CHANGE IMPORTANT
            userembed = discord.Embed(
                title="Username: ",
                description=usrname,
                color=discord.Color.red()
            )
            userembed.set_thumbnail(
                url=usr.avatar_url
            )
            userembed.set_author(
                name="User Info:"
            )
            userembed.add_field(
                name="Discriminator: ",
                value=usr.discriminator
            )
            userembed.add_field(
                name="User ID: ",
                value=usr.id
            )
            userembed.add_field(
                name="Action: ",
                value=action
            )

            cur.execute("SELECT logchannel FROM server_data WHERE serverid=?", (guild,))
            conn.commit()
            server = cur.fetchone()[0]
            logc = self.bot.get_channel(int(server))
            await logc.send(embed=userembed)
            return True

    async def logmsgaccepted(self, usr, usrname, guild):
        if usrname != "Rules Bot":  # CHANGE IMPORTANT
            userembed = discord.Embed(
                title="Username: ",
                description=usrname,
                color=discord.Color.green()
            )
            userembed.set_thumbnail(
                url=usr.avatar_url
            )
            userembed.set_author(
                name="User Info:"
            )
            userembed.add_field(
                name="Discriminator: ",
                value=usr.discriminator
            )
            userembed.add_field(
                name="User ID: ",
                value=usr.id
            )
            userembed.add_field(
                name="Action: ",
                value="User accepted on server"
            )

            cur.execute("SELECT logchannel FROM server_data WHERE serverid=?", (guild,))
            conn.commit()
            server = cur.fetchone()[0]
            logc = self.bot.get_channel(int(server))
            await logc.send(embed=userembed)
            return True


def setup(bot: commands.Bot):
    bot.add_cog(ManualCog(bot))
