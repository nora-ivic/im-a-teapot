import pytest
from sqlalchemy.orm import sessionmaker

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


@pytest.fixture(scope="session")
def test_session():
    EngineManager.set_database(settings.TEST_DATABASE)
    Base.metadata.create_all(EngineManager.get_engine())

    test_session_mold = sessionmaker(bind=EngineManager.get_engine())
    test_session = test_session_mold()
    yield test_session

    test_session.rollback()
    test_session.close()
    Base.metadata.drop_all(EngineManager.get_engine())
    EngineManager.unset_database()
