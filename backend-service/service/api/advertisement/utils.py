from typing import List

from service.api.advertisement.models import AdvertisementOutputShort
from service.repository.mappers import Advertisement, UserCustom, Pet, Picture


def map_to_output_advert_short(db_advert: Advertisement):
    user: UserCustom = db_advert.user_posted
    pet: Pet = db_advert.pet_posted
    pictures: List[Picture] = db_advert.picture_posted

    return AdvertisementOutputShort(
        advert_id=db_advert.id,
        username=user.username,
        pet_name=pet.name,
        picture_link=pictures[0].link if pictures[0] else None
    )