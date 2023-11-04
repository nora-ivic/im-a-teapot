from passlib.context import CryptContext
from sqlalchemy.orm import Session
from mappers import UserAuth, UserCustom
from typing import Optional
from service.repository.engine_manager import get_session
from service.exceptions import InvalidLoginException


class AuthorizationRepository:
    def __init__(self, session: Optional[Session]):
        self.pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
        self.session = session if session else get_session()

    def _get_existing_user(self, username: str):
        return self.session.query(UserAuth).filter(UserAuth.username == username).first()

    def check_existing_user(self, username: str):
        if self._get_existing_user(username):
            return True
        return False

    def save_new_user(self, user_signup: UserSignup):
        new_custom_user = UserCustom(
            username=user_signup.username,
            name=user_signup.name,
            surname=user_signup.surname,
            email=user_signup.email.lower()
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

        if not self.pwd_context.verify(user_login.password, existing_user.password):
            raise InvalidLoginException
