import sched
import time

import discord
from discord import Embed, Color
from discord.ext import commands
from discord.ext.commands import CommandNotFound, MissingPermissions

import KEYS
from checks import NotADeveloper
from database import *

conn = db
cur = conn.cursor()
s = sched.scheduler(time.time, time.sleep)


def get_server_prefix(bot: commands.Bot, message: discord.Message):
    if not message.guild:
        return '-'
    server_settings: ServerSettings = ServerSettings.get_or_insert(message.guild)
    return commands.when_mentioned_or(server_settings.prefix)(bot, message)


client: commands.Bot = commands.Bot(
    command_prefix=get_server_prefix,
)

client.remove_command('help')

db.create_tables([ServerData, ServerSettings, UserData, ReactionAction])


async def on_command_error(ctx: commands.Context, exc: BaseException):
    print('command error')
    if isinstance(exc, CommandNotFound):
        pass
        print('Unknown command')
        # await ctx.send(exc.args[0])
        # TODO: uncomment again. currently the manual commands block this.
    if isinstance(exc, MissingPermissions):
        await ctx.send(
            embed=Embed(
                description='You need to be an admin to execute this command - sorry',
                color=Color.red()))
    if isinstance(exc, NotADeveloper):
        await ctx.send(
            embed=Embed(
                description='Oh nice you found an dev only command, but sorry only for devs!',
                color=Color.red()))
    else:
        pass
        #raise exc


client.on_command_error = on_command_error

MODULES = [
    'modules.eval',
    'modules.reactions',
    'modules.manual_commands',
    'modules.guild',
    'modules.stop',
    'modules.info',
]

for module in MODULES:
    client.load_extension(module)

client.run(KEYS.TOKEN)
