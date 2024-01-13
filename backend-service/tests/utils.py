from sqlalchemy.orm import Session

from service.repository.mappers import UserCustom, UserAuth


def mock_user_token():
    return 1


def mock_unregistered():
    return None


def user_factory(user_id: int, session: Session):
    user = (
        UserCustom(
                id=user_id,
                username=('test_username_' + str(user_id)),
                is_shelter=False,
                email='test_email',
                phone_number='test_number'
        )
    )
    user_auth = UserAuth(username=('test_username_' + str(user_id)), password='1234')
    session.add(user)
    session.add(user_auth)
    session.commit()
    return user
