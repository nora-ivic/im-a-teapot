from pydantic import BaseModel


class AdvertisementOutputShort(BaseModel):
    pet_name: str = None
    username: str
    picture: str = None

