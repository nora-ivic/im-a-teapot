from starlette.testclient import TestClient

from service.api.authorization.utils import validate_token
from service.main import app
from tests.utils import user_factory, advert_factory, mock_user_token


class TestMessaging:

    def test_add_and_fetch_message(self, test_session):
        app.dependency_overrides[validate_token] = mock_user_token
        user_factory(1, test_session)
        user_factory(2, test_session)
        advert_factory(1, 1, test_session)

        with TestClient(app) as client:
            result = client.post(
                "/api/messages/1/add",
                json={
                    "text": "message_1"
                }
            )
            assert result.status_code == 200

            client.post(
                "/api/messages/1/add",
                json={
                    "text": "message_2"
                }
            )
            assert result.status_code == 200

            result = client.get(
                "/api/messages/1"
            )
            assert result.status_code == 200
            assert len(result.json()) == 2

