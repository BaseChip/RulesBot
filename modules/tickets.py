import discord
from discord import Guild, Embed, Color, TextChannel, NotFound, Forbidden, Role, Member, Message, utils
from discord.ext import commands
from discord.ext.commands import Context as CommandContext, Group
from discord.raw_models import RawReactionActionEvent
import re

from database import *

class TicketCog(object):
    def __init__(self, bot: commands.Bot):
        self.bot: commands.Bot = bot

    @commands.command()
    async def create_ticket(self, ctx: CommandContext, *, ticket):
        guild: Guild = ctx.guild
        self_member: Member = guild.get_member(self.bot.user.id)
        settings: ServerSettings = ServerSettings.get_or_insert(guild)
        data: ServerData = ServerData.get_or_insert(guild)
        gugui = self.bot.get_guild(385848724628439062)
        chan = gugui.get_channel(488728300101828618)
        embed = Embed(color=0x00ff00, title=f"Ticket from {ctx.author.name} on {ctx.guild.name}", description=f"Message ```{ticket}```")
        embed.add_field(name="Prefix", value=settings.prefix)
        if data.setupcomplete == "YES":
            if data.action == "ban":
                if self_member.guild_permissions.ban_members:
                    embed.add_field(name="Action", value="BAN", inline=False)
                else:
                    embed.add_field(name="Action", value="BAN: Perms missing", inline=False)
                    embed.color = Color.red()
            elif data.action == "kick":
                if self_member.guild_permissions.kick_members:
                    embed.add_field(name="Action", value="KICK", inline=False)
                else:
                    embed.add_field(name="Action", value="KICK: Perms missing", inline=False)
                    embed.color = Color.red()
            else:
                embed.add_field(name="Action", value=f"Unknown action: {data.action}", inline=False)
            channel: TextChannel = guild.get_channel(int(data.ruleschannel))
            if channel is not None:
                perms = channel.permissions_for(self_member)
                if perms.send_messages and perms.embed_links:
                    embed.add_field(name="Rules Channel", value=f'<#{data.ruleschannel}>', inline=False)
                else:
                    embed.add_field(name="Rules Channel" ,value=f'<#{data.ruleschannel}>: Missing Write permissions', inline=False)
                    embed.color = Color.red()

                try:
                    await channel.get_message(int(data.messageid))
                    embed.add_field(name="Rules Message", value='Found', inline=False)
                except NotFound:
                    embed.add_field(name="Rules Message", value=f'{data.messageid}: Missing', inline=False)
                    embed.color = Color.red()
                except Forbidden:
                    embed.add_field(name="Rules Message", value=f'{data.messageid}: Unreadable/Missing', inline=False)
                    embed.color = Color.red()
            else:
                embed.add_field(name="Rules Channel", value=f"{data.ruleschannel}: Missing", inline=False)
                embed.color = Color.red()
            log_channel: TextChannel = guild.get_channel(int(data.logchannel))
            if log_channel is not None:
                perms = log_channel.permissions_for(self_member)
                if perms.send_messages and perms.embed_links:
                    embed.add_field(name="Log Channel", value=f'<#{data.logchannel}>: All perms', inline=False)
                else:
                    embed.color = Color.red()
                    embed.add_field(name="Log Channel", value=f'<#{data.logchannel}>: Missing perms', inline=False)
            else:
                embed.add_field(name="Log Channel", value=f"{data.logchannel}: Missing", inline=False)
                embed.color = Color.red()
            try:
                invite = await ctx.channel.create_invite(max_age=0, max_uses=0, temporary=False, reason="Created a ticket"
                                                                                                      " so that our mod"
                                                                                                      "s can join your "
                                                                                                      "server for help")
                embed.add_field(name="Invite", value=invite)
            except discord.HTTPException:
                await ctx.send(":x: To create an ticket please give me invite link permission so that one of my"
                               " supporter can join")
                embed.add_field(name="Invite", value="cant create")

        gugui = self.bot.get_guild(385848724628439062)
        supporters = [member for member in gugui.members if
                      utils.get(member.roles, id=488367350274326528)]
        premium = False
        for us in supporters:
            if ctx.author.id == us.id:
                premium = True
                break
        embed.set_footer(text=f"user: {ctx.author.id}")
        if premium:
            gugui = self.bot.get_guild(385848724628439062)
            chan = gugui.get_channel(488765119363284992)
            tick = await chan.send(embed=embed)
            await chan.send("<@&464068303422226432>")
            await tick.add_reaction("✅")
            res = Embed(color=Color.green(), title=":white_check_mark: successfully sent message", description=f"We have send the message to our modertartion and support Team! You got premium support so your problem has a height priority")
            await ctx.message.channel.send(embed=res)
        elif not premium:
            gugui = self.bot.get_guild(385848724628439062)
            chan = gugui.get_channel(488728300101828618)
            tick = await chan.send(embed=embed)
            await tick.add_reaction("✅")
            res = Embed(color=Color.green(), title=":white_check_mark: successfully sent message", description=f"We have send the message to our moderation and support Team - if you would like you can join our support server [here](https://discord.gg/HD7x2vx) - we should message you back asap if you would like to get faster support please have a look at our premium plan ({ctx.prefix}premium), because we first answer all tickets created with our premium plan ;)")
            await ctx.message.channel.send(embed=res)

    async def on_raw_reaction_add(self, event: RawReactionActionEvent):
        message_id = event.message_id
        channel_id = event.channel_id
        user_id = event.user_id
        emoji = event.emoji
        gu: TextChannel = self.bot.get_channel(channel_id)
        guild: int = gu.guild.id
        channel: TextChannel = self.bot.get_channel(channel_id)
        message: Message = await channel.get_message(message_id)

        mod_list = [239719151403401216, 310702108997320705, 137259132305539072, 333220752117596160, 477141528981012511,
                    155751731471319050, 252394151717502976, 261805872332800001]
        if int(channel_id) == 488728300101828618 or channel_id == 488765119363284992:
            print("1")
            if str(emoji) == "✅" and user_id in mod_list:
                print("2")
                usr = self.bot.get_user(user_id)
                gugui = self.bot.get_guild(385848724628439062)
                embed: Embed = message.embeds[0]
                print(3)
                Guild_id = re.compile("\d\d\d\d\d\d\d\d\d\d\d\d\d\d\d\d\d\d")
                match = Guild_id.search(embed.footer.text)
                if not match:
                 return
                print(match.group())
                msg_usr = self.bot.get_user(int(match.group()))
                await msg_usr.send(f"Hey, our supporter {usr.name} is joining your server soon to help you!")
                await channel.send(f"{usr.name} is helping on ticket {message_id}")
                await usr.send("Okay thanks for helping!")


def setup(bot: commands.Bot):
    bot.add_cog(TicketCog(bot))
