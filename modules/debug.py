import discord
from discord import Guild, Embed, Color, TextChannel, NotFound, Forbidden, Role, Member
from discord.ext import commands
from discord.ext.commands import Context as CommandContext, Group

from database import *


class DebugCog(object):
    def __init__(self, bot: commands.Bot):
        self.bot: commands.Bot = bot

    @commands.group(name='data', invoke_without_command=True)
    async def debug(self, ctx: CommandContext):
        pass

    debug: Group = debug

    @debug.command()
    async def guild(self, ctx: CommandContext):
        guild: Guild = ctx.guild
        self_member: Member = guild.get_member(self.bot.user.id)
        settings: ServerSettings = ServerSettings.get_or_insert(guild)
        data: ServerData = ServerData.get_or_insert(guild)
        embed = Embed(
            color=Color.red()
        ) \
            .add_field(name='Prefix', value=settings.prefix)
        if data.setupcomplete == "YES":
            embed.color = Color.green()
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
            role: Role = discord.utils.get(guild.roles, id=data.roleid)
            if role is None:
                embed.color = Color.red()
                embed.add_field(name="Role", value=f"{data.roleid}: Missing", inline=False)
            else:
                if self_member.top_role <= role:
                    embed.add_field(name="Role", value=f"{role.mention}: Role to low", inline=False)
                    embed.color = Color.red()
                else:
                    embed.add_field(name="Role", value=role.mention, inline=False)
        await ctx.send(embed=embed)


def setup(bot: commands.Bot):
    bot.add_cog(DebugCog(bot))
