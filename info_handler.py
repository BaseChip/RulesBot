import discord


async def handle_info(message: discord.Message):
    embed = discord.Embed(title="Bot by:", description="BaseChip, RomanGreaf, Skidder", color=0x27fcfc)
    embed.set_author(name="Bot Info", icon_url="https://thebotdev.de/assets/img/Fragezeichen.png")
    embed.add_field(name="Project", value="TheBotDev", inline=True)
    embed.add_field(name="Logo/website designed by:", value="tobimori", inline=False)
    embed.add_field(name="Support server:", value="https://discord.gg/HD7x2vx", inline=False)
    embed.add_field(name="Invite me to your server:", value="-invite", inline=True)
    embed.set_footer(text="Thanks for using!")
    await message.channel.send(embed=embed)
