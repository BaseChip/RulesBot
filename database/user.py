from discord import Member
from peewee import *

from .db import db


class UserData(Model):
    usrname = CharField()
    usrid = IntegerField(primary_key=True)
    server = IntegerField()
    jointime = CharField()
    status = CharField(null=True)
    lasttime = CharField(null=True)

    class Meta:
        database = db
        db_table = 'user_data'

    @classmethod
    def get_or_insert(cls, user: Member):
        model, created = cls.get_or_create(usrid=user.id, defaults={
            'usrname': user.name,
            'server': user.guild.id,
            'jointime': user.joined_at,
        })
        if created:
            model.save()
        return model
