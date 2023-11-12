from fastapi import APIRouter, Depends, Query
from typing import Annotated, Optional

from service.api.advertisement.models import AdvertisementFilter
from service.api.authorization.utils import validate_token
from service.repository.advertisement_repo import AdvertisementRepository

advert_router = APIRouter()


@advert_router.get('/')
def home_page(
        user_id: Annotated[int, Depends(validate_token)],
        filter: Optional[AdvertisementFilter] = None,
        page: Annotated[int, Query(gt=0, le=1000)] = 1,
        page_size: Annotated[int, Query(gt=0, le=100)] = 5
):
    repo = AdvertisementRepository()
    return repo.get_adverts(filter, user_id, page, page_size)
