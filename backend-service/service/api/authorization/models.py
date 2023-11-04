from pydantic import BaseModel
from typing import Optional


class UserLogin(BaseModel):
    username: str
    password: str


class UserSignup(UserLogin):
    is_shelter: bool
    first_name: Optional[str] = None
    last_name: Optional[str] = None
    shelter_name: Optional[str] = None
    email: str
    phone_number: str
    address: Optional[str] = None
