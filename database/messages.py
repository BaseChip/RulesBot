from peewee import *

from .db import db


class ReactionAction(Model):
    emoji = CharField()
    message_id = CharField()
    role_id = CharField()

    class Meta:
        primary_key = CompositeKey('message_id', 'emoji')
        database = db
