from datetime import datetime
from typing import List

from pydantic import BaseModel

from service.enums import AdvertisementCategory, PetSpecies


class AdvertisementOutputShort(BaseModel):
    advert_id: int
    advert_category: AdvertisementCategory
    pet_name: str
    username: str
    picture_link: str = None


class AdvertisementOutputFull(BaseModel):
    advert_id: int
    advert_category: AdvertisementCategory
    pet_name: str
    pet_species: PetSpecies = None
    pet_color: str = None
    pet_age: int = None
    date_time_lost: datetime = None
    location_lost: str = None
    description: str = None
    is_in_shelter: bool = False
    username: str
    picture_links: List[str] = []
