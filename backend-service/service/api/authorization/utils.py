from datetime import datetime, timedelta
import settings
import jwt

from service.repository.authorization_repo import AuthorizationRepository
from service.repository.mappers import UserCustom
from typing import Annotated
from fastapi import Header, HTTPException


def generate_token(user: UserCustom):
    expire = datetime.now() + timedelta(minutes=settings.TOKEN_EXPIRE_MINUTES)

    to_encode = {
        "id": user.id,
        "username": user.username,
        "exp": str(expire)
    }
    encoded_jwt = jwt.encode(to_encode, settings.SECRET_KEY, algorithm=settings.ALGORITHM)

    return encoded_jwt


def validate_token(authorization: Annotated[str, Header()]):
    try:
        decoded = jwt.decode(authorization, settings.SECRET_KEY, algorithms=[settings.ALGORITHM])
        token_username: str = decoded.get("username")

        repo = AuthorizationRepository()
        if not repo.check_existing_user(token_username):
            raise jwt.InvalidTokenError

    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Expired token")

    except jwt.InvalidTokenError:
        raise HTTPException(status_code=401, detail="Invalid token")

    return decoded["user_id"], decoded["username"]
