from datetime import date
from typing import Optional

from fastapi import Query
from pydantic import BaseModel
from typing import Annotated

from service.enums import AdvertisementCategory, PetSpecies


class AdvertisementFilter(BaseModel):
    advert_category: Annotated[Optional[AdvertisementCategory], Query()] = None
    pet_name: Annotated[Optional[str], Query()] = None
    pet_species: Annotated[Optional[PetSpecies], Query()] = None
    pet_color: Annotated[Optional[str], Query()] = None
    pet_age: Annotated[Optional[int], Query()] = None
    date_time_lost: Annotated[Optional[date], Query()] = None
    location_lost: Annotated[Optional[str], Query()] = None
    description: Annotated[Optional[str], Query()] = None
    is_in_shelter: Annotated[Optional[bool], Query()] = None
    username: Annotated[Optional[str], Query()] = None
    shelter_name: Annotated[Optional[str], Query()] = None


def get_advert_filter(
    advert_category: Annotated[Optional[AdvertisementCategory], Query()] = None,
    pet_name: Annotated[Optional[str], Query()] = None,
    pet_species: Annotated[Optional[PetSpecies], Query()] = None,
    pet_color: Annotated[Optional[str], Query()] = None,
    pet_age: Annotated[Optional[int], Query()] = None,
    date_time_lost: Annotated[Optional[date], Query()] = None,
    location_lost: Annotated[Optional[str], Query()] = None,
    description: Annotated[Optional[str], Query()] = None,
    is_in_shelter: Annotated[Optional[bool], Query()] = None,
    username: Annotated[Optional[str], Query()] = None,
    shelter_name: Annotated[Optional[str], Query()] = None,
) -> AdvertisementFilter:
    return AdvertisementFilter(
        advert_category=advert_category,
        pet_age=pet_age,
        pet_name=pet_name,
        pet_species=pet_species,
        pet_color=pet_color,
        date_time_lost=date_time_lost,
        location_lost=location_lost,
        description=description,
        is_in_shelter=is_in_shelter,
        username=username,
        shelter_name=shelter_name,
    )


