import time
import traceback
import urllib.parse as urllib
from asyncio import AbstractEventLoop
from asyncio import sleep

from aiohttp import ClientSession
from discord.ext.commands import Bot

#from config import config
# disabled for github
# if you would like to enable it add the code back and enter it to the config and the config.py

class StatusPage:
    def __init__(self, client):
        self.api_base = 'https://api.statuspage.io'
        self.client = client

    async def on_ready(self):
        loop: AbstractEventLoop = self.client.loop
        loop.create_task(self.init())

    async def init(self):
        while True:
            try:
                insert_here = "insert the data i comment out here"
                session = ClientSession(loop=self.client.loop)
                headers = {"Content-Type": "application/x-www-form-urlencoded",
                           "Authorization": "OAuth " + insert_here}#+ config.sp_api_key}
                value = int(self.client.latency * 1000)
                params = urllib.urlencode({'data[timestamp]': time.time(), 'data[value]': value})
                async with session.post(f'{self.api_base}/v1/pages/{insert_here}/metrics/'
                                        f'{insert_here}'
                                        f'/data.json', headers=headers, data=params) as r:
                                        # config.sp_page_id
                                        #config.sp_metric_id
                    if r.status is not 201:
                        response = await r.text()
                        print(f"Error while sending data to status page {response}")
            except:
                traceback.print_exc()
            await sleep(60)


def setup(bot: Bot):
    bot.add_cog(StatusPage(bot))
