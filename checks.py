from discord import Member
from discord.ext import commands
from discord.ext.commands import Context as CommandContext, MissingPermissions

#ADMINS = [
 #   333220752117596160,
 #   368800541393813505,
#]


class NotADeveloper(Exception):
    pass


class MissingPermissions(Exception):
    pass


def is_developer():
    def predicate(ctx: CommandContext):
        if ctx.message.author.id != 333220752117596160:
            raise NotADeveloper("Oh you have found an dev only command, but hey devs only ;)")

    return commands.check(predicate)


def admin_permissions():
    def predicate(ctx: CommandContext):
        author: Member = ctx.message.author
        if author.id == 333220752117596160:
            return True
        elif author.guild_permissions.administrator == False:
            raise MissingPermissions("You are missing administrator permissions")
        else:
            return True

    return commands.check(predicate)