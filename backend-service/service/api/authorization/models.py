from pydantic import BaseModel, Field
from typing import Optional


class UserLogin(BaseModel):
    username: str
    password: str


class UserSignup(UserLogin):
    is_shelter: bool = False
    first_name: Optional[str] = None
    last_name: Optional[str] = None
    shelter_name: Optional[str] = None
    email: str = Field(pattern='^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
    phone_number: str
    address: Optional[str] = None
