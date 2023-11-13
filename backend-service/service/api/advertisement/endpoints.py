from fastapi import APIRouter, Depends, Query
from typing import Annotated, Optional, List

from service.api.advertisement.models import AdvertisementOutputShort
from service.api.advertisement.filters import AdvertisementFilter
from service.api.advertisement.utils import map_to_output_advert_short
from service.api.authorization.utils import validate_token
from service.repository.advertisement_repo import AdvertisementRepository

advert_router = APIRouter()


@advert_router.get('/')
def home_page(
        user_id: Annotated[int, Depends(validate_token)],
        # filter: Annotated[AdvertisementFilter, Query()] = None,
        page: Annotated[int, Query(gt=0, le=1000)] = 1,
        page_size: Annotated[int, Query(gt=0, le=100)] = 5
) -> List[AdvertisementOutputShort]:
    repo = AdvertisementRepository()
    adverts = repo.get_adverts(user_id, page, page_size)
    return_adverts = []

    #for advert in adverts:
       # return_adverts.append(map_to_output_advert_short(advert))

    return adverts
