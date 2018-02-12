import discord
import sqlite3
import time
import datetime
import sched
import aiohttp
import json
#from discord import *
import KEYS
conn = sqlite3.connect("rulesbot.db")
cur = conn.cursor()
s = sched.scheduler(time.time, time.sleep)


class MyClient(discord.Client):
	async def on_ready(self):
		cur.execute("CREATE TABLE IF NOT EXISTS user_data(usrname TEXT, usrid INTEGER, server INTEGER, jointime TEXT, status TEXT, lasttime TEXT)")
		cur.execute("CREATE TABLE IF NOT EXISTS server_data(servername TEXT, serverid INTEGER, ruleschannel INTEGER, ruletext TEXT, joinmsg TEXT, shjoin TEXT, kickmsg TEXT, kick TEXT, reactime INTEGER, logchannel INTEGER, roleid INTEGER, messageid INTEGER, action TEXT, setupcomplete TEXT)")

		game = discord.Game(name="!help")
		await client.change_presence(status=discord.Status.online, game=game)
		print("Login erfolgreich!")
		

	async def on_message(self, message):
		if message.content.startswith(KEYS.PREFIX):
			invoke = message.content[1:].split(" ")[0]
			args = message.content.split(" ")[1:]
			if invoke=="info":
				embed=discord.Embed(title="Bot by:", description="BaseChip", color=0x27fcfc)
				embed.set_author(name="Bot Info", icon_url="https://thebotdev.de/assets/img/Fragezeichen.png")
				embed.add_field(name="Project", value="TheBotDev", inline=True)
				embed.add_field(name="Support server:", value="https://discord.gg/HD7x2vx", inline=False)
				embed.add_field(name="Invite me to your server:", value="!invite", inline=True)
				embed.add_field(name="other", value="This is a fork from my GitHub Bot RulesBot")
				embed.set_footer(text="Thanks for using!")
				await message.channel.send(embed=embed)
			if invoke == "ping":
				await message.channel.send(embed=discord.Embed(color=discord.Color.magenta(), description="I am online and working hard..."))
			if invoke=="donate":
				await message.channel.send(embed=discord.Embed(color=discord.Color.orange(), description="[Patreon](https://www.patreon.com/TheBotDev)"))
			if invoke=="invite":
				embed=discord.Embed(title="Click to invite", url="https://discordapp.com/api/oauth2/authorize?client_id=389082834670845952&permissions=268634182&scope=bot", color=0x27fcfc)
				embed.set_author(name="Recommend me to others ")
				await message.channel.send(embed=embed)
			if invoke == "help":
				embed=discord.Embed(title="All Commands", url="https://discord.gg/HD7x2vx", description="For more help click on this above me to get to my Discord Support Server", color=0x27fcfc)
				embed.set_author(name="Help | Rules Bot")
				embed.add_field(name="Prefix", value="!", inline=False)
				embed.add_field(name="Help Message", value="!help", inline=False)
				embed.add_field(name="Setup the Rules", value="!setup", inline=False)
				embed.add_field(name="Edit the Rules", value="!editmessage", inline=False)
				embed.add_field(name="Donate", value="!donate", inline=True)
				embed.add_field(name="Invite the Bot", value="!invite", inline=False)
				embed.add_field(name="Bot info", value="!info  -  this is a fork from GitHub", inline=True)
				embed.set_footer(text="Bot by BaseChip | TheBotDev Project | GitHub Clone")
				await message.channel.send(embed=embed)
			if invoke == "setup" and message.author.guild_permissions.administrator == True:
				await message.channel.send(embed=discord.Embed(color=discord.Color.blue(), description="The setup for creating the server rules has been started successfully. First of all, please send me the **channel id** of the channel where you want the rules to appear later, if you don't know how this works please have a look at the screenshot. Your Channel-ID should look like this: 372344015950970885 Btw you have to activate Developer Mode see link two\n  **Links:**  https://i.imgur.com/Z7rRMtP.png \n https://i.imgur.com/oR9KjKK.png"))
				def c(m):
					if m.author.id == message.author.id and m.channel.id == message.channel.id:
						return m
				cid = await client.wait_for("message", check=c, timeout=None)
				channelid = int(cid.content)
				await message.channel.send(embed=discord.Embed(color=discord.Color.blue(), description="Okay i have the channel id now please send me the rules as text. **Markdown**: http://markdown.thebotdev.de"))
				rul = await client.wait_for("message", check=c, timeout=None)
				rulestext = rul.content
						#await message.channel.send(content="ID: %s\n Message: %s"%(str(channelid), rulestext))
				#kanall = client.get_channel(int(channelid))
				#await kanall.send(content="Test")
				await message.channel.send(embed=discord.Embed(color=discord.Color.blue(), description="Well, what should happen if a user does not accept the rules or does not react to them? I can kick or ban those users if you want me to kick the user please write 1 and if you want me to ban him please write 2. and only once as a comment please answer only with 1 or 2 and nothing else."))
				act = await client.wait_for("message", check=c, timeout=None)
				if act.content == "1":
					action = "kick"
				elif act.content == "2":
					action = "ban"
				if action != None:
					await message.channel.send(embed=discord.Embed(color=discord.Color.blue(), description="How long should the user have time to react? 5min/10min/15min/20min. Please answer with 5 for 5 minutes, 10 for 10 minutes, 15 for 15 minutes and 20 for 20 minutes - thank you!"))
					rt = await client.wait_for("message", check=c, timeout=None)
					if rt.content=="5":
						rtime = 5
					if rt.content=="10":
						rtime = 10
					if rt.content=="15":
						rtime = 15
					if rt.content=="20":
						rtime = 20
					roles = ""
					for role in message.author.guild.roles:
						roles= roles + role.name + " - " + str(role.id) + '\n'
					await message.channel.send(embed=discord.Embed(color=discord.Color.blue(), description="Almost done! This is the penultimate question. What role should I assign to the user if he or she accepts the rules? Please send me the roles id, in order to get them I send you here once all roles with the corresponding id:\n" + roles))
					ruleid = await client.wait_for("message", check=c, timeout=None)
					rid = ruleid.content
					await message.channel.send(embed=discord.Embed(color=discord.Color.blue(), description="Do you want a notification for new members on join? y/n"))
					yon = await client.wait_for("message", check=c, timeout=None)
					cyon = yon.content
					if cyon == "y" or cyon=="Y" or cyon=="Yes" or cyon=="yes" or cyon=="j" or cyon=="J":
						await message.channel.send(embed=discord.Embed(color=discord.Color.blue(), description="So please send me the on join message for the user"))
						def c(m):
							if m.author.id == message.author.id and m.channel.id == message.channel.id:
								return m
						onjoin = await client.wait_for("message", check=c, timeout=None)
						onjoinmsg = onjoin.content
						msgforjoin = "Yes"
					elif cyon == "n" or cyon == "N" or cyon == "No" or cyon=="no":
						msgforjoin = "No"
						onjoinmsg = ""
					await message.channel.send(embed=discord.Embed(color=discord.Color.blue(), description="Do you want a message for users who decline the rules? y/n"))
					onk = await client.wait_for("message", check=c, timeout=None)
					onkick = onk.content
					if onkick == "y" or onkick == "Y" or onkick == "Yes" or onkick == "yes" or onkick == "j" or onkick == "J":
						await message.channel.send(embed=discord.Embed(color=discord.Color.blue(), description="Okay please send me now the text the user should get"))
						def c(m):
							if m.author.id == message.author.id and m.channel.id == message.channel.id:
								return m
						waitforkickmessage = await client.wait_for("message", check=c, timeout=None)
						msgforkick = "Yes"
						delmes = waitforkickmessage.content
					elif onkick == "n" or onkick == "N" or onkick == "No" or onkick == "no":
						msgforkick = "No"
						delmes = ""
					await message.channel.send(embed=discord.Embed(color=discord.Color.blue(), description="Well the very last question now and then the setup is done! Please send me now the channel id of the channel where I should send all log files in, i. e. who has not accepted the rules."))
					lch = await client.wait_for("message", check=c, timeout=None)
					lchannel = int(lch.content)
					await message.channel.send(embed=discord.Embed(color=discord.Color.green(), description="SETUP DONE - THANK YOU FOR YOUR TIME").set_footer(text="Actually does the auto kick/ban after a specified time didnt work - comes with an update"))
					await message.channel.send(embed=discord.Embed(title="Please ensure the following aspects to ensure that the bot functions correctly", description="What matters is that:\n -the role of the bot lies above the role to be distributed\n -the bot has the right to kick/ban users\n -The bot has the rights to write in the channels. if you want to change the message afterwards you can do this with the command !editmessage (see !help)", color=0x27fcfc).set_author(name="Some usefull informations", url="http://thebotdev.de", icon_url="https://thebotdev.de/assets/img/Fragezeichen.png"))

					
					rulesmsg = client.get_channel(int(channelid))
					rules = await rulesmsg.send(embed=discord.Embed(color=discord.Color.green(), description=rulestext ).set_footer(text="Please accept the rules with ✅ or decline them with ❌ "))
					await rules.add_reaction("✅")
					await rules.add_reaction("❌") #cross mark
					rulesid = rules.id
					#msgid = client.get_channel(int(rulesid))
					serverid = message.author.guild.id
					setupcomplete = "YES"
					try:
						cur.execute("UPDATE server_data SET ruleschannel=?, ruletext=?, action=?, reactime=?, logchannel=?, roleid=?, messageid=?, setupcomplete=?, joinmsg=?, shjoin=?, kickmsg=?, kick=? WHERE serverid=? ",(channelid, rulestext, action, rtime, lchannel, rid,rulesid,setupcomplete, onjoinmsg, msgforjoin, delmes, msgforkick, serverid,))
						conn.commit()
					except:
						await message.channel.send(embed=discord.Embed(color=discord.Color.red(), description="When writing the data to the database an error has occurred either I have internal problems or your entered data is not correct so please try again and make sure that the replies **no** sign is needed and only the reply is sent and that you entered the correct data i. e. when asked for the ID you have really sent a channelid ~ sorry for the error").set_thumbnail(url="https://thebotdev.de/assets/img/alert.png"))
			
			
			if invoke=="editmessage":
				await message.channel.send(embed=discord.Embed(color=discord.Color.gold(), description="Ok please send me now the new message and i change it for you"))
				def check(m):
					if m.author.id == message.author.id and m.channel.id == message.channel.id:
						return m
				newmessage = await client.wait_for("message", check=check, timeout=None)
				guildid=message.author.guild.id
				nmsg = newmessage.content
				cur.execute("UPDATE server_data SET ruletext=? WHERE serverid=?", (nmsg, guildid))
				conn.commit()
				cur.execute("SELECT ruleschannel FROM server_data WHERE serverid=?", (guildid,))
				d = cur.fetchone()[0]
				rch = client.get_channel(int(d))
				n = await rch.send(embed=discord.Embed(color=discord.Color.green(), description=nmsg).set_footer(text="Please accept the rules with ✅ or decline them with ❌ "))
				id = n.id
				cur.execute("UPDATE server_data SET messageid=? WHERE serverid=?", (id, guildid))
				conn.commit()
				await n.add_reaction("✅")
				await n.add_reaction("❌") #cross mark
				
				
			elif invoke == "setup" and message.author.guild_permissions.administrator == False:
				await message.channel.send(embed=discord.Embed(color=discord.Color.red(),description="This command can only use administrators, sorry").set_thumbnail(url="https://thebotdev.de/assets/img/alert.png"))


				
	async def on_raw_reaction_add(self, emoji, message_id, channel_id, user_id):
		msg = message_id
		gu = client.get_channel(channel_id)
		guild = gu.guild.id
		gugui = client.get_guild(guild)
		usr = gugui.get_member(user_id)
		user = client.get_user(user_id)
		server = gugui
		cur.execute("SELECT messageid From server_data WHERE serverid=?", (guild,))
		ruleschannel = cur.fetchone()[0]
		if message_id == ruleschannel:
			#print("So weit so gut emoji noch nic´ht erkannt")
			if str(emoji)=="✅": #up
				usa = client.get_user(user_id)
				usrname = usa.name
				userid = usa.id
				cur.execute("SELECT roleid FROM server_data WHERE serverid=?", (guild,))
				conn.commit()
				role = cur.fetchone()[0]
				arole = discord.utils.get(gugui.roles, id=role)
				
				await usr.add_roles(arole, reason="accepted the rules", atomic=True)
				await logmsgaccepted(usr, usrname, guild) #user username guild
				cur.execute("UPDATE user_data SET status='accepted' WHERE usrname=? AND usrid=?", (usrname, userid))
			elif str(emoji)=="❌": #down
				cur.execute("SELECT action FROM server_data WHERE messageid=?", (str(msg),))
				action = cur.fetchone()[0]
				##print(action)
				if action=="kick":
					usa = client.get_user(user_id)
					usrname = usa.name
					userid = usa.id
					unix = int(time.time())
					now = str(datetime.datetime.fromtimestamp(unix).strftime('%H:%M'))
					a = "kick"
					await logmsg(usr, usrname, now, a, guild)
					if usa.name != "Rules Bot":	#CHANGE IT HER IS IMPORTANT
						cur.execute("SELECT kick FROM server_data WHERE serverid=?", (guild,))
						sisam = cur.fetchone()[0]
						if sisam=="Yes":
							cur.execute("SELECT kickmsg FROM Server_data WHERE serverid=?", (guild,))
							msgtext = cur.fetchone()[0]
							try:
								await usa.send(embed=discord.Embed(color=discord.Color.red(), description=msgtext))
							except:
								pass
						await usr.kick()
						cur.execute("UPDATE user_data SET status=? WHERE usrname=? AND usrid=?", (a, usrname, userid))
						conn.commit()
				elif action=="ban":
					unix = int(time.time())
					now = str(datetime.datetime.fromtimestamp(unix).strftime('%H:%M'))
					usa = client.get_user(user_id)
					usrname = usa.name
					userid = usa.id
					a = "ban"
					await logmsg(usr, usrname, now, a, guild)
					if usa.name != "Rules Bot":
						cur.execute("SELECT kick FROM server_data WHERE serverid=?", (guild,))
						sisam = cur.fetchone()[0]
						if sisam=="Yes":
							cur.execute("SELECT kickmsg FROM Server_data WHERE serverid=?", (guild,))
							msgtext = cur.fetchone()[0]
							await usa.send(embed=discord.Embed(color=discord.Color.red(), description=msgtext))
						await usr.ban(reason="Declined the rules")
						cur.execute("UPDATE user_data SET status='banned' WHERE usrname=? AND usrid=?", (usrname, userid))
						conn.commit()

			

				
			
			
			
	async def on_guild_join(self, guild):
		gid = guild.id
		gnm = guild.name
		cur.execute('SELECT * FROM server_data WHERE serverid=?', (gid,))
		data = cur.fetchall()
		counter = 0
		for row in data:
			counter +=1
		if counter == 0:
			#first join to this server
			cur.execute("INSERT INTO server_data(servername, serverid) VALUES(?, ?)",(gnm, gid))
			conn.commit()
		elif counter != 0:
			#rejoin a server
			pass
		




	async def on_member_join(self, member):
		usrid = member.id
		usrname = member.name
		unix = int(time.time())
		guild = member.guild.id
		status = "undefined"
		jointime = str(datetime.datetime.fromtimestamp(unix).strftime('%H:%M'))
		cur.execute("SELECT * FROM user_data WHERE usrid=? AND server=? ", (usrid, guild))
		#conn.commit()
		usrdata = cur.fetchall()
		counterusr = 0
		for row in usrdata:
			counterusr +=1
			#print(row)
		if counterusr == 0:
			cur.execute("INSERT INTO user_data(usrid, usrname, jointime, server, status) VALUES(?, ?, ?, ?, ?)",(usrid, usrname, jointime, guild, status))
			conn.commit()
		elif counterusr != 0:
			cur.execute("UPDATE user_data SET jointime=? WHERE usrid=? AND server=? ", (jointime, usrid, guild))
			conn.commit()
			##print("NOT NONE")
		cur.execute("SELECT shjoin FROM server_data WHERE serverid=?", (int(guild),))
		conn.commit()
		join = cur.fetchone()[0]
		if join=="Yes":
			cur.execute("SELECT joinmsg FROM server_data WHERE serverid=?", (int(guild),))
			m = cur.fetchone()[0]
			await member.send(embed=discord.Embed(color=0x27fcfc, description=m))
			
#_______
#\     /
# \   /
#  \ /

#   O

async def logmsg(usr, usrname, time, action, guild):
	if usrname != "Rules Bot":    #CHANGE IMPORTANT
		userembed=discord.Embed(
			title="Username: ",
			description=usrname,
			color=discord.Color.red()
		)
		userembed.set_thumbnail(
			url=usr.avatar_url
		)
		userembed.set_author(
			name="User Info:"
		)
		userembed.add_field(
			name="Discriminator: ",
			value=usr.discriminator
		)
		userembed.add_field(
			name="User ID: ",
			value=usr.id
		)
		userembed.add_field(
			name="Action: ",
			value=action
		)
		#userembed.add_field(
		#	name="Created at:"
		#	value=usr.created_at #You could add but it dosnt look nice
		#)

		cur.execute("SELECT logchannel FROM server_data WHERE serverid=?", (guild,))
		conn.commit()
		server = cur.fetchone()[0]
		logc= client.get_channel(int(server))
		await logc.send(embed=userembed)
		return True
		
#_______
#\     /
# \   /
#  \ /
#   O
		
async def logmsgaccepted(usr, usrname, guild):
	if usrname != "Rules Bot":    #CHANGE IMPORTANT
		userembed=discord.Embed(
			title="Username: ",
			description=usrname,
			color=discord.Color.green()
		)
		userembed.set_thumbnail(
			url=usr.avatar_url
		)
		userembed.set_author(
			name="User Info:"
		)
		userembed.add_field(
			name="Discriminator: ",
			value=usr.discriminator
		)
		userembed.add_field(
			name="User ID: ",
			value=usr.id
		)
		userembed.add_field(
			name="Action: ",
			value="User accepted on server"
		)

		cur.execute("SELECT logchannel FROM server_data WHERE serverid=?", (guild,))
		conn.commit()
		server = cur.fetchone()[0]
		logc= client.get_channel(int(server))
		await logc.send(embed=userembed)
		return True


client=MyClient()
client.run(KEYS.TOKEN)


