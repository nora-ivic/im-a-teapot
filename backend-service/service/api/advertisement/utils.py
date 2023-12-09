from typing import List

from service.api.advertisement.models import AdvertisementOutputShort, AdvertisementOutputFull
from service.repository.mappers import Advertisement, UserCustom, Pet, Picture


def map_to_output_advert_short(db_advert: Advertisement):
    user: UserCustom = db_advert.user_posted
    pet: Pet = db_advert.pet_posted
    pictures: List[Picture] = db_advert.picture_posted

    return AdvertisementOutputShort(
        advert_id=db_advert.id,
        advert_category=db_advert.category,
        username=user.username,
        pet_name=pet.name if pet.name else '?',
        picture_link=pictures[0].link if pictures[0] else None
    )


def map_to_output_advert_full(db_advert: Advertisement):
    user: UserCustom = db_advert.user_posted
    pet: Pet = db_advert.pet_posted
    pictures: List[Picture] = db_advert.picture_posted

    return AdvertisementOutputFull(
        advert_id=db_advert.id,
        advert_category=db_advert.category,
        pet_name=pet.name if pet.name else '?',
        pet_species=pet.species if pet.species else None,
        pet_color=pet.color,
        pet_age=pet.age,
        date_time_lost=pet.date_time_lost,
        location_lost=pet.location_lost,
        description=pet.description,
        is_in_shelter=db_advert.is_in_shelter,
        username=user.username,
        picture_links=[picture.link for picture in pictures]
    )
