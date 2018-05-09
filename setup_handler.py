import discord
from discord.ext.commands import Paginator

from database import ServerData, db

def is_yes_answer(text):
    return text.lower() in ['y', 'yes', 'j']


async def handle_setup(message: discord.Message, client: discord.Client):
    channel: discord.TextChannel = message.channel
    guild: discord.Guild = message.guild
    await channel.send(
        embed=discord.Embed(
            color=discord.Color.blue(),
            description="The setup for creating the server rules has been started successfully. First of all, "
                        "please send me the **channel** of the channel where you want the rules to appear later "
                        "as mention (#yourchannel)"))

    def check_author(m):
        if m.author.id == message.author.id and m.channel.id == message.channel.id:
            return m

    cid = await client.wait_for("message", check=check_author, timeout=None)
    try:
        chani = cid.channel_mentions[0]
        channelid = chani.id
    except:
        await message.channel.send(embed=discord.Embed(color=discord.Color.red(),
                                                           description="ERROR: You didn`t mentioned a channel"))
        return
    await message.channel.send(
        embed=discord.Embed(
            color=discord.Color.blue(),
            description="Okay i have the channel id now please send me the rules as text. **Markdown**: "
                        "http://markdown.thebotdev.de"))
    rul = await client.wait_for("message", check=check_author, timeout=None)
    rulestext = rul.content
    await message.channel.send(
        embed=discord.Embed(
            color=discord.Color.blue(),
            description="Well, what should happen if a user does not accept the rules or does not react to them? I "
                        "can kick or ban those users if you want me to kick the user please write 1 and if you want "
                        "me to ban him please write 2. and only once as a comment please answer only with 1 or 2 and "
                        "nothing else."))
    act = await client.wait_for("message", check=check_author, timeout=None)
    action = None
    if act.content == "1":
        action = "kick"
    elif act.content == "2":
        action = "ban"
    if action is not None:
        roles = '\n'.join(role.name + " - " + str(role.id) for role in guild.roles)
        try:
            await channel.send(
                embed=discord.Embed(
                    color=discord.Color.blue(),
                    description="Almost done! This is the penultimate question. What role should I assign to the user "
                                "if he or she accepts the rules? Please send me the roles id, in order to get them I "
                                "send you here once all roles with the corresponding id:\n" + roles))
        except discord.Forbidden:
            await channel.send(
                embed=discord.Embed(
                    color=discord.Color.blue(),
                    description="Almost done! This is the penultimate question. What role should I assign to the user "
                                "if he or she accepts the rules? Please send me the roles id, in order to get them I "
                                "send you here once all roles with the corresponding id:"))
            paginator = Paginator()
            for line in roles.split('\n'):
                paginator.add_line(line)
            for page in paginator.pages:
                await channel.send(
                    embed=discord.Embed(
                        color=discord.Color.blue(),
                        description=page))

        ruleid = await client.wait_for("message", check=check_author, timeout=None)
        rid = ruleid.content
        await message.channel.send(
            embed=discord.Embed(
                color=discord.Color.blue(),
                description="Do you want a notification for new members on join? y/n"))
        yon = await client.wait_for("message", check=check_author, timeout=None)
        cyon = yon.content.lower()
        msgforjoin = "No"
        onjoinmsg = ""
        if is_yes_answer(cyon):
            await message.channel.send(
                embed=discord.Embed(
                    color=discord.Color.blue(),
                    description="So please send me the on join message for the user"))

            onjoin = await client.wait_for("message", check=check_author, timeout=None)
            onjoinmsg = onjoin.content
            msgforjoin = "Yes"
        await message.channel.send(
            embed=discord.Embed(
                color=discord.Color.blue(),
                description="Do you want a message for users who decline the rules? y/n"))
        onk = await client.wait_for("message", check=check_author, timeout=None)
        onkick = onk.content
        msgforkick = "No"
        delmes = ""
        if is_yes_answer(onkick):
            await message.channel.send(
                embed=discord.Embed(
                    color=discord.Color.blue(),
                    description="Okay please send me now the text the user should get"))
            waitforkickmessage = await client.wait_for("message", check=check_author, timeout=None)
            msgforkick = "Yes"
            delmes = waitforkickmessage.content
        await message.channel.send(
            embed=discord.Embed(
                color=discord.Color.blue(),
                description="Well the very last question now and then the setup is done! Please mention now the "
                            "channel where I should send all log files in, i. e. who has not "
                            "accepted the rules or who removed the reaction."))
        lch = await client.wait_for("message", check=check_author, timeout=None)
        try:
            chanx = lch.channel_mentions[0]
            lchannel = chanx.id
        except:
            await message.channel.send(embed=discord.Embed(color=discord.Color.red(),
                                                           description="ERROR: You didn`t mentioned a channel"))
            return
        await message.channel.send(
            embed=discord.Embed(
                color=discord.Color.green(),
                description="SETUP DONE - THANK YOU FOR YOUR TIME"))
        await message.channel.send(
            embed=discord.Embed(
                title="Please ensure the following aspects to ensure that the bot functions correctly",
                description="What matters is that:\n -the role of the bot lies above the role to be distributed\n "
                            "-the bot has the right to kick/ban users\n -The bot has the rights to write in the "
                            "channels. if you want to change the message afterwards you can do this with the command "
                            "!editmessage (see !help)",
                color=0x27fcfc).set_author(name="Some usefull informations", url="http://thebotdev.de",
                                           icon_url="https://thebotdev.de/assets/img/Fragezeichen.png"))
        try:
            rulesmsg = client.get_channel(int(channelid))
        except:
            await message.channel.send(embed=discord.Embed(color=discord.Color.red(),
                                                           description="ERROR: I cant send messages to the channel for"
                                                                       "the rules"))
            return
        try:
            rules = await rulesmsg.send(
                embed=discord.Embed(
                    color=discord.Color.green(),
                    description=rulestext).set_footer(
                    text="Please accept the rules with ✅ or decline them with ❌ "))
        except discord.Error.Forbidden:
            await message.channel.send(embed=discord.Embed(color=discord.Color.red(),
                                                           description="ERROR: I cant write in the channel for the "
                                                                       "rules"))
            return
        try:
            await rules.add_reaction("✅")
            await rules.add_reaction("❌")
        except:
            await message.channel.send(embed=discord.Embed(color=discord.Color.red(),
                                                           description="ERROR: I cant add reaction in the channel for "
                                                                       "the rules"))
            return
        try:
            loggingmessagechannel = client.get_channel(int(lchannel))
        except:
            await message.channel.send(embed=discord.Embed(color=discord.Color.red(),
                                                           description="ERROR: Cant find the logging channel!"))
            return
        if loggingmessagechannel.permissions_for(client.user).send_messages == False:
            await message.channel.send(embed=discord.Embed(color=discord.Color.red(),
                                                           description="ERROR: I have no perms to write in " +
                                                                        (loggingmessagechannel.mention)))
        rulesid = rules.id
        serverid = message.author.guild.id
        setupcomplete = "YES"
        try:
            ServerData.create(
                rulechannel=channelid,
                ruletext=rulestext,
                action=action,
                logchannel=lchannel,
                roleid=rid,
                messageid=rulesid,
                setupcomplete=setupcomplete,
                joinmsg=onjoinmsg,
                shjoin=msgforjoin,
                kickmsg=delmes,
                kick=msgforkick,
                serverid=serverid,
            ).save()
            db.commit()
        except:
            await message.channel.send(
                embed=discord.Embed(
                    color=discord.Color.red(),
                    description="When writing the data to the database an error has occurred either I have internal "
                                "problems or your entered data is not correct so please try again ;) - sorry"
                                "sorry for the error").set_thumbnail(
                    url="https://thebotdev.de/assets/img/alert.png"))
