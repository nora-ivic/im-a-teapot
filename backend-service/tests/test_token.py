from service.api.authorization.utils import generate_token, validate_token
from service.repository.mappers import UserCustom


class TestToken:

    def test_decode_token(self):
        user = UserCustom(
            id=1,
            username="string",
            is_shelter=False,
            first_name="string",
            last_name="string",
            email="email@gmail.com",
            phone_number="098765432",
        )

        token = generate_token(user)

        id = validate_token(token)

        assert id == 1
