import discord

from database import ServerData, db


async def handle_editmessage(message: discord.Message, client: discord.Client, server_data: ServerData):
    channel: discord.TextChannel = message.channel
    author: discord.Member = message.author
    await channel.send(
        embed=discord.Embed(
            color=discord.Color.gold(),
            description="Ok please send me now the new message and i change it for you"))

    def check(m):
        if m.author.id == author.id and m.channel.id == channel.id:
            return m

    newmessage = await client.wait_for("message", check=check, timeout=None)
    nmsg = newmessage.content
    server_data.ruletext = nmsg
    rules_channel_id = server_data.ruleschannel
    rules_channel = client.get_channel(int(rules_channel_id))
    new_message = await rules_channel.send(
        embed=discord.Embed(
            color=discord.Color.green(),
            description=nmsg
        ).set_footer(
            text="Please accept the rules with ✅ or decline them with ❌ "))
    server_data.messageid = new_message.id
    server_data.save()
    db.commit()
    await new_message.add_reaction("✅")
    await new_message.add_reaction("❌")
