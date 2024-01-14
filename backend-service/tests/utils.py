from sqlalchemy.orm import Session

from service.repository.mappers import UserCustom, UserAuth, Pet, Advertisement


def mock_user_token():
    return 1


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


def advert_factory(id: int, user_id: int, session: Session):
    pet = Pet(
        id=id,
        name=('test_pet_' + str(id)),
        location_lost=('test_location_' + str(id)),
    )
    advert = Advertisement(
        id=id,
        pet_id=id,
        user_id=user_id,
        category='lost'
    )
    session.add(pet)
    session.add(advert)
    session.commit()
