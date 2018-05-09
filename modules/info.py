from datetime import datetime, timedelta

from discord import TextChannel, Color, Embed, Message
from discord.ext import commands
from discord.ext.commands import Context as CommandContext


class InfoCog(object):
    def __init__(self, bot: commands.Bot):
        self.bot: commands.Bot = bot

    @commands.command()
    async def ping(self, ctx: CommandContext):
        channel: TextChannel = ctx.channel
        message: Message = ctx.message
        now = datetime.utcnow()
        ping: timedelta = now - message.created_at
        ping = ping.microseconds / 1000
        await channel.send(
            embed=Embed(
                color=Color.magenta(),
                description='My Ping is `{:.2f}ms`'.format(ping)))


def setup(bot: commands.Bot):
    bot.add_cog(InfoCog(bot))
