from fastapi import APIRouter
from service.api.authorization.models import UserLogin, UserSignup

auth_router = APIRouter()


@auth_router.post("/login")
def log_in(user_login: UserLogin):
    return


@auth_router.post("/signup")
def log_in(user_login: UserSignup):
    return
