from pydantic import BaseModel


class AdvertisementOutputShort(BaseModel):
    pet_name: str
    username: str
    picture: str

