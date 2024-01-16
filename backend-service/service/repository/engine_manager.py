from sqlalchemy.orm import Session
from sqlalchemy import URL, Engine, create_engine

import settings


class EngineManager:
    database = settings.DEFAULT_DATABASE
    engine = None

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
        if not cls.engine:
            cls.engine = create_engine(EngineManager.engine_wrapper())
        return cls.engine

    @classmethod
    def set_database(cls, database: str) -> None:
        EngineManager.database = database
        EngineManager.engine = None

    @classmethod
    def unset_database(cls) -> None:
        EngineManager.database = settings.DEFAULT_DATABASE
        EngineManager.engine = None


def get_session() -> Session:
    return Session(EngineManager.get_engine())
