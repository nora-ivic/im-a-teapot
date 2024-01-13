from tests.utils import user_factory
from service.api.authorization.utils import generate_token, validate_token


class TestAuthentication:

    def test_auth_utils(self, test_session):
        user = user_factory(1, test_session)
        token = generate_token(user)
        assert len(token) == 181

        decoded = validate_token(token)
        assert decoded == user.id

    def test_signup_and_login(self):
        assert True
        # TODO napravit signup, testirat, napravit login, testirat
