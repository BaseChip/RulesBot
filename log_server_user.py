import discord
import KEYS

class MyClient(discord.Client):
    async def on_ready(self):
        file = open("server.txt", "w")
        cg = 0
        for guild in client.guilds:
            file.write("%s - %s User \n" % (guild.name, len(guild.members)))
            cg = cg + 1
        file.write("============")
        file.write("Servercount: %s" % (str(cg)))
        print("fertig")
        file.close()

client = MyClient()
client.run(KEYS.TOKEN)