from discord import Member
from discord.ext import commands
from discord.ext.commands import Context as CommandContext, MissingPermissions

ADMINS = [
    333220752117596160,
    310702108997320705,
]


class NotADeveloper(Exception):
    pass


def is_developer():
    def predicate(ctx: CommandContext):
        if ctx.author.id in ADMINS:
            return True
        raise NotADeveloper("Oh you have found an dev only command, but hey devs only ;)")

    return commands.check(predicate)


def admin_permissions():
    def predicate(ctx: CommandContext):
        author: Member = ctx.message.author
        if author.id in ADMINS:
            return True
        elif not author.guild_permissions.administrator:
            raise MissingPermissions("You are missing administrator permissions")
        else:
            return True

    return commands.check(predicate)
