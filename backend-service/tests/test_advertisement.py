from starlette.testclient import TestClient

from service.api.authorization.utils import validate_token
from service.main import app
from tests.utils import mock_user_token, user_factory


class TestHome:

    def test_filter__by_pet_name_and_username(self):
        assert True
        # TODO napravit create x2 i filtirat

    def test_filter__by_location(self):
        assert True
        # TODO napravit create x2 i filtrirat po lokaciji - fail


class TestAdvertActions:

    def test_create(self, test_session, mocker):
        app.dependency_overrides[validate_token] = mock_user_token
        user_factory(1, test_session)
        with TestClient(app) as client:
            result = client.post(
                "/api/advert/create",
                json={
                    "pet_name": "Edgar",
                    # TODO - stavit jos atributa i testirat ih ispod
                }
            )
            assert result.status_code == 200

    def test_edit(self):
        assert True
        # TODO - ponovit create i napravit edit i onda testirat

    def test_delete(self):
        assert True
        # TODO - ponovit create i napravit delete i onda testirat deleted
