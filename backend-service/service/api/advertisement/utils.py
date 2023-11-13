from typing import Tuple

from service.api.advertisement.models import AdvertisementOutputShort


def map_to_output_advert_short(data: Tuple):
    return AdvertisementOutputShort(
        username=data[0],
        pet_name=data[1],
        picture_link=data[2]
    )