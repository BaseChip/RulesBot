import sys

from discord.ext import commands
from discord.ext.commands import Context as CommandContext

import checks


def setup(bot: commands.Bot):
    @bot.command()
    @checks.is_developer()
    async def stop(ctx: CommandContext):
        sys.exit(0)
