import json
from typing import Annotated, List

from fastapi import APIRouter, HTTPException, Response, Depends, Query
from service.api.authorization.models import UserLogin, UserSignup, ShelterOutput
from service.api.authorization.utils import generate_token, validate_signup, validate_token, map_to_output_shelter
from service.exceptions import InvalidLoginException
from service.repository.authorization_repo import AuthorizationRepository

auth_router = APIRouter()


@auth_router.post("/login")
def log_in(user_login: UserLogin):
    repo = AuthorizationRepository()

    try:
        current_user = repo.login_user(user_login)

    except InvalidLoginException:
        raise HTTPException(status_code=401, detail="Username or password incorrect!")

    token = generate_token(current_user)
    repo.session.close()

    return {
        "token": token,
        "is_shelter": current_user.is_shelter,
        "current_user_username": current_user.username,
        "current_user_email": current_user.email,
        "current_user_phone_number": current_user.phone_number
    }


@auth_router.post("/signup")
def sign_up(user_signup: UserSignup):
    validate_signup(user_signup)

    repo = AuthorizationRepository()

    if repo.check_existing_user(user_signup.username):
        raise HTTPException(status_code=400, detail="Username already exists")

    repo.save_new_user(user_signup)
    repo.session.close()

    return Response(status_code=201, content=json.dumps({"detail": "User successfully saved"}))


@auth_router.get("/shelters")
def get_shelters(
        user_id: Annotated[int, Depends(validate_token)],
        page: Annotated[int, Query(gt=0, le=100)] = 1,
        page_size: Annotated[int, Query(gt=0, le=100)] = 5
) -> List[ShelterOutput]:
    repo = AuthorizationRepository()
    db_shelters = repo.get_shelters(page, page_size)
    output_shelters = []

    for db_shelter in db_shelters:
        output_shelters.append(map_to_output_shelter(db_shelter))

    repo.session.close()
    return output_shelters
