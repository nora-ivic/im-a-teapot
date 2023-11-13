from pydantic import BaseModel


class AdvertisementOutputShort(BaseModel):
    advert_id: int
    pet_name: str = None
    username: str
    picture: str = None

