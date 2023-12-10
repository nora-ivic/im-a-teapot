from fastapi import APIRouter, Depends, Query, HTTPException
from typing import Annotated, Optional, List

from service.api.advertisement.models import AdvertisementOutputShort, AdvertisementOutputFull
from service.api.advertisement.filters import AdvertisementFilter
from service.api.advertisement.utils import map_to_output_advert_short, map_to_output_advert_full
from service.api.authorization.utils import validate_token
from service.repository.advertisement_repo import AdvertisementRepository

advert_router = APIRouter()


@advert_router.get('/')
def home_page(
        user_id: Annotated[int, Depends(validate_token)],
        # filter: Annotated[AdvertisementFilter, Query()] = None,
        page: Annotated[int, Query(gt=0, le=100)] = 1,
        page_size: Annotated[int, Query(gt=0, le=100)] = 5
) -> List[AdvertisementOutputShort]:
    repo = AdvertisementRepository()
    db_adverts = repo.get_adverts(user_id, page, page_size)
    output_adverts = []

    for db_advert in db_adverts:
        output_adverts.append(map_to_output_advert_short(db_advert))

    return output_adverts


@advert_router.get('/{advert_id}/details')
def details(
        advert_id: int,
        user_id: Annotated[int, Depends(validate_token)]
) -> AdvertisementOutputFull:
    repo = AdvertisementRepository()
    db_advert = repo.get_advert_by_id(advert_id)

    if not db_advert:
        raise HTTPException(status_code=400, detail='Advert not found!')

    output_advert = map_to_output_advert_full(db_advert)

    return output_advert
