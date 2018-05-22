import discord
import KEYS
import aiohttp
from discord import Webhook, AsyncWebhookAdapter
import os

class MyClient(discord.Client):
    async def on_ready(self):
        print("online")

    async def on_member_update(self, before, after):
        if before.id == 389082834670845952:
            print(after.status)
            if before.status != after.status:
                print("Check 1")
                if after.status is discord.Status.offline:
                    print("Check 2")
                    async with aiohttp.ClientSession() as session:
                        webhook = Webhook.from_url(
                            'https://discordapp.com/api/webhooks/447108409741934603/-Gndf8ERENjpzA-0nCVka0dLq_1FMop5RVxGsw4C0otSv20lCAnpB4Pi2Iozq08Dr3ZI',
                            adapter=AsyncWebhookAdapter(session))
                        await webhook.send("RulesBot ist offline gegangen")
                    os.system('screen -dmS rulesbotautoinstance python3.6 /home/falk/rewrite/main.py')
                    async with aiohttp.ClientSession() as session:
                        webhook = Webhook.from_url(
                            'https://discordapp.com/api/webhooks/447108409741934603/-Gndf8ERENjpzA-0nCVka0dLq_1FMop5RVxGsw4C0otSv20lCAnpB4Pi2Iozq08Dr3ZI',
                            adapter=AsyncWebhookAdapter(session))
                        await webhook.send("Wieder online")


client = MyClient()
client.run(KEYS.ONLINE)