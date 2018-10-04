from collections import defaultdict
from datetime import datetime, timedelta

from discord import TextChannel, Color, Embed, Message, utils, Guild, Forbidden
from discord.ext import commands
from discord.ext.commands import Context as CommandContext, Paginator

import checks
from awaiter import AdvancedAwaiter
from database import *
from help_handler import handle_help
from info_handler import handle_info


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

    @commands.command()
    @checks.is_developer()
    async def botinfo(self, ctx: CommandContext):
        embed = Embed(color=0x80ffff)
        embed.add_field(name="Guilds", value=str(len(self.bot.guilds)), inline=False)
        embed.add_field(name="User", value=str(len(self.bot.users)))
        await ctx.channel.send(embed=embed)

    @commands.command()
    @checks.is_developer()
    async def update(self, ctx: CommandContext, *, txt):
        owners = defaultdict(list)
        for guild in self.bot.guilds:
            owners[guild.owner.id].append(guild.name)
        for owner, guilds in owners.items():
            try:
                await self.bot.get_user(owner).send(
                    embed=Embed(
                        description=txt
                                    + "\n\nYou are getting this message because you are owner of the following guilds: "
                                    + "\n".join(guilds),
                        color=Color.green(),
                    )
                )
            except Forbidden:
                pass
        await ctx.send(content="Done. Sent the update text.")

    @commands.command()
    async def invite(self, ctx: CommandContext):
        await ctx.channel.send(embed=Embed(color=Color.green(),
                                           description=""" So you like to invite me to your server? [Click here](https://discordapp.com/oauth2/authorize?client_id=389082834670845952&permissions=268634182&scope=bot "Greg did something, too. Certainly.") to invite me ;)"""))

    @commands.command()
    async def help(self, ctx: CommandContext):
        server_settings: ServerSettings = ServerSettings.get_or_insert(ctx.guild)
        await handle_help(message=ctx.message, server_settings=server_settings)

    @commands.command()
    async def info(self, ctx: CommandContext):
        await handle_info(message=ctx.message)

    @commands.command()
    async def supporter(self, ctx: CommandContext):
        gugui = self.bot.get_guild(385848724628439062)
        supporters = [member for member in gugui.members if
                      utils.get(member.roles, id=388689757909024769)]
        supporter = ""
        for us in supporters:
            supporter = f"{supporter}\n{us.name}#{us.discriminator}"
        await ctx.channel.send(
            f"Here is a list of all generous donors who have supported the bot here:```{supporter}```")

    @commands.command()
    @checks.admin_permissions()
    async def edit_message(self, ctx: CommandContext):
        server_data: ServerData = ServerData.get_or_insert(ctx.guild)
        awaiter: AdvancedAwaiter = AdvancedAwaiter(ctx)
        new_rules = await awaiter.text("Ok please send me now the new message and i change it for you")

        server_data.ruletext = new_rules
        server_data.save()

        rules_channel = self.bot.get_channel(server_data.ruleschannel)
        old_message: Message = await rules_channel.get_message(server_data.messageid)

        old_embed: Embed = old_message.embeds[0]
        old_embed.description = new_rules

        await old_message.edit(embed=old_embed)
        await ctx.send(
            embed=Embed(
                description="Successfully updated the rules!",
                color=Color.green()))

    @commands.command()
    @commands.guild_only()
    async def roles(self, ctx: CommandContext):
        guild: Guild = ctx.guild
        paginator = Paginator()
        for role in guild.roles:
            paginator.add_line(role.name + ' ' + str(role.id))

        for page in paginator.pages:
            await ctx.send(
                embed=Embed(
                    color=Color.blurple(),
                    description=page))


def setup(bot: commands.Bot):
    bot.add_cog(InfoCog(bot))
