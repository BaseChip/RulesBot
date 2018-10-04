from configlib import BaseConfig


class Config(BaseConfig):
    token: str


config = Config.get_instance()
