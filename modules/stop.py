import sys

from discord.ext import commands
from discord.ext.commands import Context as CommandContext


def setup(bot: commands.Bot):
    @bot.command()
    @commands.is_owner()
    async def stop(ctx: CommandContext):
        return
        #sys.exit(0)
