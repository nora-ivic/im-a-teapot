from datetime import datetime
from typing import List, Optional

from pydantic import BaseModel

from service.enums import AdvertisementCategory, PetSpecies


class AdvertisementOutputShort(BaseModel):
    advert_id: int
    advert_category: AdvertisementCategory
    pet_name: str
    username: str
    picture_link: Optional[str] = None


class AdvertisementOutputFull(BaseModel):
    advert_id: int
    advert_category: AdvertisementCategory
    pet_name: str
    pet_species: Optional[PetSpecies] = None
    pet_color: Optional[str] = None
    pet_age: Optional[int] = None
    date_time_lost: Optional[datetime] = None
    location_lost: Optional[str] = None
    description: Optional[str] = None
    is_in_shelter: bool = False
    username: str
    picture_links: List[str] = []
    shelter_name: Optional[str] = None
    shelter_username: Optional[str] = None
    shelter_email: Optional[str] = None
    shelter_phone_number: Optional[str] = None


class AdvertisementInput(BaseModel):
    advert_category: AdvertisementCategory
    pet_name: str
    pet_species: Optional[PetSpecies] = None
    pet_color: Optional[str] = None
    pet_age: Optional[int] = None
    date_time_lost: Optional[datetime] = None
    location_lost: Optional[str] = None
    description: Optional[str] = None
    is_in_shelter: bool = False
    picture_links: List[str] = []