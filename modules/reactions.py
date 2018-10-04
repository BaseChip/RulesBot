import discord
from discord import Message, Role, Embed, Color, TextChannel, Guild, Member, Reaction, Emoji, PartialEmoji
from discord.abc import Snowflake
from discord.ext import commands
from discord.raw_models import RawReactionActionEvent
from peewee import ModelSelect

import checks
from awaiter import AdvancedAwaiter, AwaitCanceled, AwaitTimedOut
from database import *


class ReactionsCog:
    def __init__(self, bot: commands.Bot):
        self.bot: commands.Bot = bot

    @commands.command(name="setupreaction")
    @checks.admin_permissions()
    async def setup(self, ctx: commands.Context):
        try:
            awaiter: AdvancedAwaiter = AdvancedAwaiter(ctx)
            channel: TextChannel = await awaiter.guild_channel('Okay the setup has started! first of all mention the'
                                                               ' channel in which your message was written and where'
                                                               ' I should add the reaction. Btw. you could cancel the '
                                                               'setup with `@CANCEL@`')
            reaction_message: Message = await awaiter.as_message(
                'So now please send me the message ID from the message where the reactions should been added. You can '
                'get it by right-clicking on your message and copying it with `copy id`. This will not work until you '
                'have activated Developer mode. (https://discordia.me/developer-mode)',
                in_channel=channel)
            if len(reaction_message.reactions) > 10:
                return await ctx.send(
                    embed=Embed(
                        color=Color.red(),
                        description='Too many reactions. Canceling the setup'))
            reaction_emote: Reaction = (
                await awaiter.emoji_reaction('React to this message with the emoji I should add'
                                             ' to your message - if you want a non-standard Discord Emoji you need to'
                                             ' add this emoji to this server'))
            guild: Guild = ctx.guild
            reaction_emote_str: str = str(reaction_emote)
            reaction_emote: Emoji = reaction_emote.emoji
            if not (isinstance(reaction_emote, str) or reaction_emote.id in [emoji.id for emoji in guild.emojis]):
                return await ctx.send(
                    embed=Embed(
                        description='You need to add the emoji to this server if you do not want to use an standard'
                                    ' discord emoji',
                        color=Color.red()))
            if isinstance(reaction_emote, Snowflake):
                reaction_emote_str = reaction_emote.id
            await reaction_message.add_reaction(reaction_emote)
            action_count = ReactionAction.select().where(
                (ReactionAction.message_id == reaction_message.id) &
                (ReactionAction.emoji == str(reaction_emote))).count()
            if action_count > 0:
                await ctx.send(
                    embed=Embed(
                        description='You could cancel the setup with @CANCEL@'))
            reaction_role: Role = await awaiter.guild_role('Please mention now the role I should add the users if they'
                                                           ' click on the reaction ')
            action, created = ReactionAction.get_or_create(emoji=reaction_emote_str,
                                                           message_id=reaction_message.id,
                                                           defaults={
                                                               'role_id': reaction_role.id
                                                           })
            action: ReactionAction
            if created:
                await ctx.send(
                    embed=Embed(
                        color=Color.green(),
                        description='created'))
            else:
                action.role_id = reaction_role.id
                await ctx.send(
                    embed=Embed(
                        color=Color.green(),
                        description='Role overwriten'))
            action.save()
            db.commit()
        except AwaitTimedOut:
            await ctx.send(
                embed=Embed(
                    color=Color.red(),
                    description="You timed out."))
        except AwaitCanceled:
            await ctx.send(
                embed=Embed(
                    color=Color.red(),
                    description="You canceled."))

    async def emote_reaction_handle(self, event: RawReactionActionEvent, handle):
        message_id: str = str(event.message_id)
        emoji: PartialEmoji = event.emoji
        if emoji.is_unicode_emoji():
            emoji = str(emoji)
        else:
            emoji = emoji.id
        guild: Guild = self.bot.get_guild(event.guild_id)
        user: Member = guild.get_member(event.user_id)
        action_query: ModelSelect = ReactionAction.select().where((ReactionAction.emoji == emoji) &
                                                                  (ReactionAction.message_id == message_id))
        if not action_query.exists():
            return
        action: ReactionAction = action_query.get()
        role_id = action.role_id
        role: Role = discord.utils.get(guild.roles,
                                       id=int(role_id))
        if role is None:
            print('Role not found.')
            return
        await handle(user, role, reason='Self roles')

    async def on_raw_reaction_add(self, event: RawReactionActionEvent):
        await self.emote_reaction_handle(event, Member.add_roles)

    async def on_raw_reaction_remove(self, event: RawReactionActionEvent):
        await self.emote_reaction_handle(event, Member.remove_roles)


def setup(bot: commands.Bot):
    bot.add_cog(ReactionsCog(bot))
