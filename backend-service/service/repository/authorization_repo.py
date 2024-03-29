from passlib.context import CryptContext
from sqlalchemy.orm import Session
from service.repository.mappers import UserAuth, UserCustom
from typing import Optional
from service.repository.engine_manager import get_session
from service.exceptions import InvalidLoginException
from service.api.authorization.models import UserLogin, UserSignup


class AuthorizationRepository:
    def __init__(self, session: Optional[Session] = None):
        self.pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
        self.session = session if session else get_session()

    def _get_existing_user(self, username: str):
        return self.session.query(UserAuth).filter(UserAuth.username == username).first()

    def _get_user_custom(self, username: Optional[str] = None, user_id: Optional[int] = None):
        query = self.session.query(UserCustom)
        if username:
            query = query.filter(UserCustom.username == username)
        if user_id:
            query = query.filter(UserCustom.id == user_id)
        return query.first()

    def check_existing_user(self, username: str):
        if self._get_existing_user(username=username):
            return True
        return False

    def check_is_shelter(self, user_id):
        user = self._get_user_custom(user_id=user_id)
        return user.is_shelter

    def save_new_user(self, user_signup: UserSignup):
        new_custom_user = UserCustom(
            username=user_signup.username,
            is_shelter=user_signup.is_shelter,
            first_name=user_signup.first_name,
            last_name=user_signup.last_name,
            shelter_name=user_signup.shelter_name,
            email=user_signup.email.lower(),
            phone_number=user_signup.phone_number
        )

        self.session.add(new_custom_user)

        new_auth_user = UserAuth(
            username=user_signup.username,
            password=self.pwd_context.hash(user_signup.password)
        )

        self.session.add(new_auth_user)
        self.session.commit()

    def login_user(self, user_login: UserLogin):
        existing_user = self._get_existing_user(user_login.username)
        if not existing_user:
            raise InvalidLoginException

        if not self.pwd_context.verify(user_login.password, existing_user.password):
            raise InvalidLoginException

        return self._get_user_custom(username=existing_user.username)

    def get_shelters(
            self,
            page: int,
            page_size: int
    ):
        query = (
            self.session.query(UserCustom)
            .filter(UserCustom.is_shelter == True)
        )

        return (
            query
            .order_by(UserCustom.shelter_name)
            .limit(page_size).offset((page - 1) * page_size)
            .all()
        )
