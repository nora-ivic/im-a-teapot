import pytest
from sqlalchemy.orm import sessionmaker, close_all_sessions

import settings
from sqlalchemy_utils import create_database, drop_database
from sqlalchemy import URL

from service.repository.engine_manager import EngineManager
from service.repository.mappers import Base


def pytest_configure():
    create_database(
        URL.create(
            "postgresql",
            username=settings.POSTGRES_USER,
            password=settings.POSTGRES_PASSWORD,
            host=settings.POSTGRES_HOST,
            port=settings.POSTGRES_PORT,
            database=settings.TEST_DATABASE,
        )
    )


def pytest_unconfigure():
    drop_database(
        URL.create(
            "postgresql",
            username=settings.POSTGRES_USER,
            password=settings.POSTGRES_PASSWORD,
            host=settings.POSTGRES_HOST,
            port=settings.POSTGRES_PORT,
            database=settings.TEST_DATABASE,
        )
    )


@pytest.fixture(scope="function")
def test_session():
    EngineManager.set_database(settings.TEST_DATABASE)
    engine = EngineManager.get_engine()
    Base.metadata.create_all(engine)

    test_session_mold = sessionmaker(bind=engine, expire_on_commit=False)
    test_session = test_session_mold()
    yield test_session

    close_all_sessions()
    Base.metadata.drop_all(engine)
    EngineManager.unset_database()
