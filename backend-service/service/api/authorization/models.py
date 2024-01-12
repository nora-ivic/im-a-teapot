from pydantic import BaseModel, Field


class UserLogin(BaseModel):
    username: str = Field(min_length=5, max_length=50)
    password: str


class UserSignup(UserLogin):
    is_shelter: bool = False
    first_name: str | None = Field(default=None, max_length=50)
    last_name: str | None = Field(default=None, max_length=50)
    shelter_name: str | None = Field(default=None, max_length=100)
    email: str = Field(pattern='^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
    phone_number: str = Field(max_length=15)


class ShelterOutput(BaseModel):
    shelter_username: str = Field(min_length=5, max_length=50)
    shelter_name: str = Field(max_length=100)
    shelter_email: str = Field(pattern='^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
    shelter_phone_number: str = Field(max_length=15)
