from discord import Message, Embed, Color, TextChannel, Role
from discord.ext import commands
from discord.ext.commands import Context as CommandContext
from peewee import ModelSelect

import checks
from awaiter import AdvancedAwaiter, AwaitCanceled, AwaitTimedOut
from database import *


class GuildManagement(object):

    def __init__(self, bot: commands.Bot):
        self.bot = bot

    @commands.command()
    @checks.admin_permissions()
    @commands.guild_only()
    async def restore(self, ctx: CommandContext):
        server_data: ServerData = ServerData.get_or_insert(ctx.guild)
        server_settings: ServerSettings = ServerSettings.get_or_insert(ctx.guild)

        if server_data.ruleschannel is None:
            await ctx.send(content="You first need to setup some rules with %ssetup" % server_settings.prefix)
            return
        chanid = server_data.ruleschannel
        chan = self.bot.get_channel(chanid)
        text = server_data.ruletext
        rules = await chan.send(
            embed=Embed(
                color=Color.green(),
                description=text
            ).set_footer(
                text="Please accept the rules with ✅ or decline them with ❌"))
        await rules.add_reaction("✅")
        await rules.add_reaction("❌")

    @commands.command()
    @checks.admin_permissions()
    @commands.guild_only()
    async def setup(self, ctx: CommandContext):
        try:
            awaiter: AdvancedAwaiter = AdvancedAwaiter(ctx)
            existing_rules: ModelSelect = ServerData.select().where(ServerData.serverid == ctx.guild.id)
            if existing_rules.exists():  # TODO: Warnungstext verbessern
                await ctx.send('Warning overwrite stuff')
            rules_channel: TextChannel = await awaiter.guild_channel(
                "The setup for creating the server rules has been started successfully. First of all, please send me the "
                "**channel** of the channel where you want the rules to appear later as mention (#yourchannel)",
                writable=True)
            rules_text: str = await awaiter.text(
                "Okay i have the channel id now please send me the rules as text. **Markdown**: "
                "http://markdown.thebotdev.de")
            actions = {
                '🔨': 'ban',
                '❌': 'kick',
            }
            action: str = await awaiter.emoji_choice(
                "Well, what should happen if a user does not accept the rules or does not react to them? I can kick "
                "or ban those users. If you want me to ban the user please react with 🔨. If you want me to kick him "
                "please react with ❌", choices=actions.keys())
            action: str = actions[action]
            role: Role = await awaiter.guild_role(
                "Almost done! This is the penultimate question. What role should I assign to the user "
                "if he or she accepts the rules?", list_ids=True)
            join_notifications: bool = await awaiter.yes_no_question(
                'Do you want a notification for new members on join? y/n')
            join_msg = ''
            if join_notifications:
                join_msg = await awaiter.text("So please send me the on join message for the user")

            decline_user_notifications: bool = await awaiter.yes_no_question(
                "Do you want a message for users who decline the rules? y/n")
            decline_user_msg = ''
            if decline_user_notifications:
                decline_user_msg: str = await awaiter.text("Okay please send me now the text the user should get")

            log_channel: TextChannel = await awaiter.guild_channel(
                "Well the very last question now and then the setup is done! Please mention now the "
                "channel where I should send all log files in, i. e. who has not "
                "accepted the rules or who removed the reaction.", writable=True)
            await ctx.send(
                embed=Embed(
                    color=Color.green(),
                    description="SETUP DONE - THANK YOU FOR YOUR TIME"))
            await ctx.send(
                embed=Embed(
                    title="Please ensure the following aspects to ensure that the bot functions correctly",
                    description="What matters is that:\n -the role of the bot lies above the role to be distributed\n "
                                "-the bot has the right to kick/ban users\n -The bot has the rights to write in the "
                                "channels. if you want to change the message afterwards you can do this with the command"
                                " !editmessage (see !help)",
                    color=0x27fcfc).set_author(name="Some usefull informations", url="http://thebotdev.de",
                                               icon_url="https://thebotdev.de/assets/img/Fragezeichen.png"))
            rules = await rules_channel.send(
                embed=Embed(
                    color=Color.green(),
                    description=rules_text).set_footer(
                    text="Please accept the rules with ✅ or decline them with ❌"))

            await rules.add_reaction("✅")
            await rules.add_reaction("❌")
            try:
                if existing_rules.exists():
                    existing_rules: ServerData = existing_rules.get()
                    existing_rules.delete_instance()

                ServerData.create(
                    ruleschannel=rules_channel.id,
                    ruletext=rules_text,
                    action=action,
                    logchannel=log_channel.id,
                    roleid=role.id,
                    messageid=rules.id,
                    setupcomplete="YES",
                    joinmsg=join_notifications,
                    shjoin=join_msg,
                    kickmsg=decline_user_msg,
                    servername=ctx.guild.name,
                    kick=decline_user_notifications,
                    serverid=ctx.guild.id,
                ).save()
                db.commit()
            except:
                await ctx.send(
                    embed=Embed(
                        color=Color.red(),
                        description="When writing the data to the database an error has occurred either I have "
                                    "internal problems or your entered data is not correct so please try again ;) - "
                                    "sorry for the error").set_thumbnail(
                        url="https://thebotdev.de/assets/img/alert.png"))
                raise
        except AwaitCanceled:
            await ctx.send(
                embed=Embed(
                    color=Color.red(),
                    description="You canceled the setup"))
        except AwaitTimedOut:
            await ctx.send(
                embed=Embed(
                    color=Color.red(),
                    description="You timed out"))

    @commands.command()
    @checks.admin_permissions()
    @commands.guild_only()
    async def pchange(self, ctx: CommandContext):
        awaiter: AdvancedAwaiter = AdvancedAwaiter(ctx)
        settings: ServerSettings = ServerSettings.get_or_insert(ctx.guild)
        mes: Message = await awaiter(
            text='Your current prefix is `%s`. Now just send me the new prefix' % settings.prefix)
        settings.prefix = mes.content
        settings.save()
        db.commit()
        await ctx.send(content="Successful set the prefix to " + settings.prefix)


def setup(bot: commands.Bot):
    bot.add_cog(GuildManagement(bot))