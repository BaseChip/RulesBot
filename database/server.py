from typing import Union

from discord import Guild
from discord.abc import Snowflake
from peewee import *

from .db import db


class ServerSettings(Model):
    gid = IntegerField(primary_key=True)
    prefix = CharField(default='-')

    @classmethod
    def get_or_insert(cls, guild: Union[Guild, int, str]):
        if isinstance(guild, Snowflake):
            guild = guild.id
        model, created = cls.get_or_create(gid=guild)
        if created:
            model.save()
        return model

    class Meta:
        database = db
        db_table = 'server_settings'


class ServerData(Model):
    servername = CharField()
    serverid = IntegerField(primary_key=True)
    ruleschannel = IntegerField(null=True)
    ruletext = CharField(null=True)
    joinmsg = CharField(null=True)
    shjoin = CharField(null=True)
    removeroleid = IntegerField(null=True)
    kickmsg = CharField(null=True)
    kick = CharField(null=True)
    reactime = IntegerField(null=True)
    logchannel = IntegerField(null=True)
    roleid = IntegerField(null=True)
    messageid = IntegerField(null=True)
    action = CharField(null=True)
    setupcomplete = CharField(null=True)

    class Meta:
        database = db
        db_table = 'server_data'

    @classmethod
    def get_or_insert(cls, guild: Guild):
        model, created = cls.get_or_create(serverid=guild.id, defaults={
            'servername': guild.name
        })
        if created:
            model.save()
        return model
