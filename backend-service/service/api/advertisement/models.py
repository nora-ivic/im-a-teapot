from pydantic import BaseModel

from service.enums import AdvertisementCategory


class AdvertisementOutputShort(BaseModel):
    advert_id: int
    advert_category: AdvertisementCategory
    pet_name: str = None
    username: str
    picture_link: str = None

