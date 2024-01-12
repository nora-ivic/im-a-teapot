from datetime import datetime, timedelta
import settings
import jwt

from service.api.authorization.models import UserSignup, ShelterOutput
from service.repository.authorization_repo import AuthorizationRepository
from service.repository.mappers import UserCustom
from typing import Annotated
from fastapi import Header, HTTPException


def validate_signup(user: UserSignup):
    if not user.is_shelter and not user.first_name:
        raise HTTPException(status_code=400, detail="First name required")

    if user.is_shelter and not user.shelter_name:
        raise HTTPException(status_code=400, detail="Shelter name required")

    if not user.is_shelter and user.shelter_name:
        raise HTTPException(status_code=400, detail="User not registered as shelter, remove shelter name")

    if user.is_shelter and (user.first_name or user.last_name):
        raise HTTPException(status_code=400, detail="Cannot save first and last name for shelter")


def generate_token(user: UserCustom):
    expire = datetime.now() + timedelta(minutes=settings.TOKEN_EXPIRE_MINUTES)

    to_encode = {
        "id": user.id,
        "username": user.username,
        "expire": str(expire)
    }
    encoded_jwt = jwt.encode(payload=to_encode, key=settings.SECRET_KEY, algorithm=settings.ALGORITHM)

    return encoded_jwt


def validate_token(authentication: Annotated[str, Header()] = None):
    if not authentication:
        return None

    try:
        decoded = jwt.decode(jwt=authentication, key=settings.SECRET_KEY, algorithms=[settings.ALGORITHM])
        token_username: str = decoded.get("username")
        repo = AuthorizationRepository()
        if not repo.check_existing_user(token_username):
            raise jwt.InvalidTokenError

        if datetime.strptime(decoded["expire"], "%Y-%m-%d %H:%M:%S.%f") < datetime.now():
            raise jwt.ExpiredSignatureError

    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Expired token")

    except jwt.InvalidTokenError:
        raise HTTPException(status_code=401, detail="Invalid token")

    return decoded["id"]


def map_to_output_shelter(db_shelter: UserCustom):

    return ShelterOutput(
        username=db_shelter.username,
        shelter_name=db_shelter.shelter_name,
        email=db_shelter.email,
        phone_number=db_shelter.phone_number
    )
