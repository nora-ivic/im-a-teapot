from sqlalchemy.orm import Session
from sqlalchemy import URL, Engine, create_engine

import settings


class EngineManager:
    database = settings.DEFAULT_DATABASE

    @classmethod
    def engine_wrapper(cls):
        return URL.create(
            "postgresql",
            username=settings.POSTGRES_USER,
            password=settings.POSTGRES_PASSWORD,
            host=settings.POSTGRES_HOST,
            port=settings.POSTGRES_PORT,
            database=cls.database,
        )

    @classmethod
    def get_engine(cls) -> Engine:
        return create_engine(EngineManager.engine_wrapper())

    @classmethod
    def set_database(cls, database: str) -> None:
        EngineManager.database = database


def get_session():
    return Session(EngineManager.get_engine())
