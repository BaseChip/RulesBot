import asyncio
import sys
import traceback
from asyncio import InvalidStateError, TimeoutError
from concurrent import futures

import discord
from discord import Embed, Color, NotFound, LoginFailure, Forbidden
from discord.ext import commands
from discord.ext.commands import MissingPermissions, CommandNotFound, MissingRequiredArgument

from checks import NotADeveloper
from config import config
from database import *

conn = db
cur = conn.cursor()
db.create_tables([ServerData, ServerSettings, UserData, ReactionAction, Token])


def get_server_prefix(bot: commands.Bot, message: discord.Message):
    if not message.guild:
        return '-'
    server_settings: ServerSettings = ServerSettings.get_or_insert(message.guild)
    return commands.when_mentioned_or(server_settings.prefix)(bot, message)


ignore_errors = (AttributeError, ValueError, NotFound, Forbidden, CommandNotFound, MissingRequiredArgument)


async def on_command_error(ctx: commands.Context, exc: BaseException):
    if isinstance(exc, ignore_errors):
        pass
    elif isinstance(exc, MissingPermissions):
        await ctx.send(
            embed=Embed(
                description='YAdmin command executed',
                color=Color.red()))
    elif isinstance(exc, NotADeveloper):
        await ctx.send(
            embed=Embed(
                description='Oh nice you found an dev only command, but sorry only for devs!',
                color=Color.red()))
    else:
        raise exc


async def on_error(*args, **kwargs):
    extype = sys.exc_info()[0]
    if issubclass(extype, ignore_errors):
        return
    traceback.print_exc()


MODULES = [
    'modules.eval',
    'modules.reactions',
    'modules.manual_commands',
    'modules.guild',
    'modules.stop',
    'modules.debug',
    'modules.info',
    'modules.premium',
    'modules.tickets',
    'modules.statuspage',
]


def generate_client(loop=None):
    client: commands.Bot = commands.AutoShardedBot(
        command_prefix=get_server_prefix,
        loop=loop
    )

    client.remove_command('help')

    for module in MODULES:
        client.load_extension(module)

    client.event(on_command_error)
    client.event(on_error)

    return client


def handle_exit(client):
    client.loop.create_task(client.logout())
    for t in asyncio.Task.all_tasks(loop=client.loop):
        if t.done():
            t.exception()
            continue
        t.cancel()
        try:
            client.loop.run_until_complete(asyncio.wait_for(t, 5, loop=client.loop))
            t.exception()
        except (InvalidStateError, TimeoutError, asyncio.CancelledError, futures.CancelledError):
            pass
        except:
            traceback.print_exc()


def main():
    loop = None
    while True:
        client = generate_client(loop)
        loop = client.loop
        try:
            loop.run_until_complete(client.start(config.token))
        except LoginFailure:
            handle_exit(client)
            loop.close()
            print("Wrong token")
            break
        except (SystemExit, KeyboardInterrupt):
            handle_exit(client)
            loop.close()
            break
        except:
            traceback.print_exc()


if __name__ == '__main__':
    main()
