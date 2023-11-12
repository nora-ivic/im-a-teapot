from fastapi import APIRouter, HTTPException, Response
from service.api.authorization.models import UserLogin, UserSignup
from service.api.authorization.utils import generate_token, validate_signup
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

    return Response(status_code=200, content={"token": token, "is_shelter": current_user.is_shelter})


@auth_router.post("/signup")
def sign_up(user_signup: UserSignup):
    validate_signup(user_signup)

    repo = AuthorizationRepository()

    if repo.check_existing_user(user_signup.username):
        return HTTPException(status_code=400, detail="Username already exists")

    repo.save_new_user(user_signup)

    return Response(status_code=201, content="User successfully saved")
