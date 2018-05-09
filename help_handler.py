import discord

from database import ServerSettings


async def handle_help(message: discord.Message, server_settings: ServerSettings):
    prefix = server_settings.prefix
    embed = discord.Embed(title="All Commands", url="https://discord.gg/HD7x2vx",
                          description="For more help click on this above me to get to my Discord Support Server",
                          color=0x27fcfc)
    embed.set_author(name="Help | Rules Bot")
    embed.add_field(name="Prefix", value=prefix, inline=False)
    embed.add_field(name="Change Prefix", value=prefix + "pchange")
    embed.add_field(name="Help Message", value=prefix + "help", inline=False)
    embed.add_field(name="Setup the Rules", value=prefix + "setup", inline=False)
    embed.add_field(name="Reaction under a message to get roles", value=prefix+"setupreaction", inline=False)
    embed.add_field(name="Edit the Rules", value=prefix + "editmessage", inline=False)
    embed.add_field(name="Restore the rules if you delete them", value=prefix + "restore", inline=False)
    embed.add_field(name="All supporter", value=prefix + "supporter", inline=True)
    embed.add_field(name="Invite the Bot", value=prefix + "invite", inline=False)
    embed.add_field(name="Bot info", value=prefix + "info", inline=True)
    embed.set_footer(text="Bot by BaseChip | TheBotDev Project")
    await message.channel.send(embed=embed)
