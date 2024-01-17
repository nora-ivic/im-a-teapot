from starlette.testclient import TestClient

from service.api.authorization.utils import validate_token
from service.main import app
from service.repository.mappers import Advertisement
from tests.utils import mock_user_token, user_factory, advert_factory


class TestHome:

    def test_filter__by_pet_name_and_username(self, test_session):
        user_factory(1, test_session)
        user_factory(2, test_session)
        advert_factory(1, 1, test_session)
        advert_factory(2, 2, test_session)

        with TestClient(app) as client:
            result = client.get(
                "/api/advert/",
                params={"pet_name": "test_pet_1", "username": "test_username_1"}
            )

            assert result.status_code == 200
            assert len(result.json()) == 1

    def test_filter__by_location(self, test_session):
        user_factory(1, test_session)
        user_factory(2, test_session)
        advert_factory(1, 1, test_session)
        advert_factory(2, 2, test_session)

        with TestClient(app) as client:
            result = client.get(
                "/api/advert/",
                params={"location_lost": "test_location_1"}
            )

            assert result.status_code == 200
            assert len(result.json()) == 1


class TestAdvertActions:

    def test_create(self, test_session):
        app.dependency_overrides[validate_token] = mock_user_token
        user_factory(1, test_session)
        with TestClient(app) as client:
            result = client.post(
                "/api/advert/create",
                json={
                    "pet_name": "Edgar",
                }
            )
            assert result.status_code == 200

    def test_edit(self, test_session):
        app.dependency_overrides[validate_token] = mock_user_token
        user_factory(1, test_session)
        advert_factory(1, 1, test_session)
        with TestClient(app) as client:
            result = client.put(
                "/api/advert/1/edit",
                json={
                    "pet_name": "Allan",
                }
            )
            assert result.status_code == 200
            assert result.json().get("pet_name") == "Allan"

    def test_delete(self, test_session):
        app.dependency_overrides[validate_token] = mock_user_token
        user_factory(1, test_session)
        advert_factory(1, 1, test_session)
        with TestClient(app) as client:
            result = client.delete(
                "/api/advert/1/delete"
            )
            assert result.status_code == 200
