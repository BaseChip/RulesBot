import datetime
import time

import aiohttp
import discord
from discord import Embed, Color, Message, TextChannel, Guild
from discord import Webhook, AsyncWebhookAdapter
from discord.ext import commands
from discord.raw_models import RawReactionActionEvent

from config import config
from database import *

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
        game = discord.Game(name="-help | Sponsored by dyn-box.de")
        await self.bot.change_presence(status=discord.Status.online, activity=game)
        print("Login erfolgreich!")
        print(len(self.bot.guilds))

    async def on_raw_reaction_add(self, event: RawReactionActionEvent):
        message_id = event.message_id
        channel_id = event.channel_id
        user_id = event.user_id
        emoji = event.emoji
        gu: TextChannel = self.bot.get_channel(channel_id)
        guild: int = gu.guild.id
        channel: TextChannel = self.bot.get_channel(channel_id)
        message: Message = await channel.get_message(message_id)
        gugui: Guild = gu.guild
        user: discord.Member = gugui.get_member(user_id)
        if user.bot:
            return
        server_data: ServerData = ServerData.get_or_insert(gugui)
        rule_message_id = server_data.messageid
        username = user.name
        userid = user.id
        roleid = server_data.roleid
        removeroleid = server_data.removeroleid
        role = discord.utils.get(gugui.roles, id=roleid)
        removerole = discord.utils.get(gugui.roles, id=removeroleid)
        user_data: UserData = UserData.get_or_insert(user)
        if server_data.messageid is None:
            return
        if int(message_id) == int(server_data.messageid):
            if str(emoji) == "✅":
                await user.add_roles(role, reason="accepted the rules", atomic=True)
                await user.remove_roles(removerole, reason="accepted the rules", atomic=True)
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
        server_data: ServerData = ServerData.get_or_insert(gugui)
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
                description="%s - %s" % (usrname, usr.mention),
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
                description="%s - %s" % (usrname, usr.mention),
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
