from starlette.testclient import TestClient

from tests.utils import user_factory
from service.api.authorization.utils import generate_token, validate_token
from service.main import app


class TestAuthentication:

    def test_auth_utils(self, test_session):
        user = user_factory(1, test_session)
        token = generate_token(user)
        assert len(token) == 181

        decoded = validate_token(token)
        assert decoded == user.id

    def test_signup_and_login(self, test_session):
        with TestClient(app) as client:
            result_signup = client.post(
                "/api/authorization/signup",
                json={
                    "username": "test_shelter",
                    "password": "password",
                    "is_shelter": True,
                    "shelter_name": "Test Shelter",
                    "email": "test@shelter.com",
                    "phone_number": "111"
                }
            )

            assert result_signup.status_code == 201
            assert result_signup.json().get("detail") == "User successfully saved"

            result_login = client.post(
                "/api/authorization/login",
                json={
                    "username": "test_shelter",
                    "password": "password"
                }
            )

            assert result_login.status_code == 200
            assert result_login.json().get("token") is not None
            assert result_login.json().get("is_shelter")
            assert result_login.json().get("current_user_username") == "test_shelter"
