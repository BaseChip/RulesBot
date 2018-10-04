from discord import Color, Embed, Member, Object
from discord.ext import commands
from discord.ext.commands import Context as CommandContext

PREMIUM_RULESBOT = 488367350274326528
ACTIVE_PATREON = 488774886043680769


class PremiumCog(object):
    def __init__(self, bot: commands.Bot):
        self.bot: commands.Bot = bot

    async def on_member_update(self, before: Member, after: Member):
        if after.guild.id != 385848724628439062:
            return
        if any(role.id == ACTIVE_PATREON for role in after.roles) and not any(
                role.id == PREMIUM_RULESBOT for role in after.roles):
            await after.add_roles(Object(id=PREMIUM_RULESBOT), reason="Patreon")

    @commands.command()
    async def premium(self, ctx: CommandContext):
        await ctx.send(
            embed=Embed(
                color=Color.green(),
                description="Hey, cool that you think about to go premium! If you go premium you would support the "
                            "developer and the moderators. Also you would help us to cover our costs ;) But what "
                            "would you get?\n__**What will you get?**__\n• change footer text\n• change embed "
                            "color\n• you can sign up to an beta from the bot to test new features\n• faster "
                            "support\n• exclusive textchannels\n__**Where to buy?**__\nYou could buy it on Patreon ["
                            "here](https://www.patreon.com/TheBotDev), but other then normally with patreon this is "
                            "an **one time payment** so you dont need to pay monthly for staying premium!"))


def setup(bot: commands.Bot):
    bot.add_cog(PremiumCog(bot))
