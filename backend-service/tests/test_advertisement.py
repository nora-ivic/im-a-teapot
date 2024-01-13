from starlette.testclient import TestClient

from service.api.authorization.utils import validate_token
from service.main import app
from service.repository.mappers import UserCustom
from tests.utils import mock_user_token


class TestCreateAdvert:

    def test_create(self, test_session, mocker):
        app.dependency_overrides[validate_token] = mock_user_token
        test_session.add(
            UserCustom(
                id=1,
                username='test_username',
                is_shelter=False,
                email='test_email',
                phone_number='test_number'
            )
        )
        test_session.commit()
        with TestClient(app) as client:
            result = client.post(
                "/api/advert/create",
                json={
                    "pet_name": "Edgar",
                }
            )

            assert result.status_code == 200