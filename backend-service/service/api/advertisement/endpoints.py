from fastapi import APIRouter, Depends, Query, HTTPException
from typing import Annotated, List

from service.api.advertisement.models import AdvertisementOutputShort, AdvertisementOutputFull, AdvertisementInput
from service.api.advertisement.filters import AdvertisementFilter, get_advert_filter
from service.api.advertisement.utils import map_to_output_advert_short, map_to_output_advert_full, validate_advert_input
from service.api.authorization.utils import validate_token
from service.exceptions import PermissionDeniedException, AdvertNotFoundException
from service.repository.advertisement_repo import AdvertisementRepository

advert_router = APIRouter()


@advert_router.get('/')
def home_page(
        user_id: Annotated[int, Depends(validate_token)],
        filter_: AdvertisementFilter = Depends(get_advert_filter),
        page: Annotated[int, Query(gt=0, le=100)] = 1,
        page_size: Annotated[int, Query(gt=0, le=100)] = 5
) -> List[AdvertisementOutputShort]:
    repo = AdvertisementRepository()
    db_adverts = repo.get_adverts(user_id, page, page_size, filter_)
    output_adverts = []

    for db_advert in db_adverts:
        output_adverts.append(map_to_output_advert_short(db_advert))

    repo.session.close()
    return output_adverts


@advert_router.get('/my_adverts')
def user_adverts(
        user_id: Annotated[int, Depends(validate_token)],
        page: Annotated[int, Query(gt=0, le=100)] = 1,
        page_size: Annotated[int, Query(gt=0, le=100)] = 5
) -> List[AdvertisementOutputShort]:
    if not user_id:
        raise HTTPException(status_code=403, detail='Only registered users have this option!')

    repo = AdvertisementRepository()
    db_user_adverts = repo.get_user_adverts(user_id, page, page_size)
    output_user_adverts = []

    for db_user_advert in db_user_adverts:
        output_user_adverts.append(map_to_output_advert_short(db_user_advert))

    repo.session.close()
    return output_user_adverts


@advert_router.get('/{advert_id}/details')
def details(
        advert_id: int,
        user_id: Annotated[int, Depends(validate_token)]
) -> AdvertisementOutputFull:
    repo = AdvertisementRepository()
    db_advert = repo.get_advert_by_id(advert_id)

    if not db_advert:
        raise HTTPException(status_code=404, detail='Advert not found!')

    output_advert = map_to_output_advert_full(db_advert)

    repo.session.close()
    return output_advert


@advert_router.post('/{advert_id}/in_shelter')
def in_shelter(
        advert_id: int,
        user_id: Annotated[int, Depends(validate_token)]
):
    repo = AdvertisementRepository()

    if not repo.is_shelter(user_id):
        raise HTTPException(status_code=403, detail='Only shelters have this option!')

    try:
        edited_advert = repo.make_sheltered(advert_id, user_id)
    except AdvertNotFoundException:
        raise HTTPException(status_code=404, detail="Advert not found!")

    output_advert = map_to_output_advert_full(edited_advert)
    repo.session.close()
    return output_advert


@advert_router.post('/create')
def create_advert(
        user_id: Annotated[int, Depends(validate_token)],
        advert_input: Annotated[AdvertisementInput, Depends(validate_advert_input)],
):
    if not user_id:
        raise HTTPException(status_code=403, detail='Only registered users have this option!')

    repo = AdvertisementRepository()

    db_advert = repo.create_advert(advert_input, user_id)
    advert_output = map_to_output_advert_full(db_advert)

    repo.session.close()
    return advert_output


@advert_router.put('/{advert_id}/edit')
def edit_advert(
        user_id: Annotated[int, Depends(validate_token)],
        advert_id: int,
        advert_input: Annotated[AdvertisementInput, Depends(validate_advert_input)],
):
    if not user_id:
        raise HTTPException(status_code=403, detail='Only registered users have this option!')

    repo = AdvertisementRepository()

    try:
        db_advert = repo.edit_advert(advert_input, advert_id, user_id)
    except PermissionDeniedException:
        raise HTTPException(status_code=403, detail="Cannot edit other user's advertisement")
    except AdvertNotFoundException:
        raise HTTPException(status_code=404, detail="Advert not found")

    advert_output = map_to_output_advert_full(db_advert)

    repo.session.close()
    return advert_output


@advert_router.delete('/{advert_id}/delete')
def delete_advert(
        user_id: Annotated[int, Depends(validate_token)],
        advert_id: int,
):
    if not user_id:
        raise HTTPException(status_code=403, detail='Only registered users have this option!')

    repo = AdvertisementRepository()

    try:
        repo.delete_advert(advert_id, user_id)
    except PermissionDeniedException:
        raise HTTPException(status_code=403, detail="Cannot delete other user's advertisement")
    except AdvertNotFoundException:
        raise HTTPException(status_code=404, detail="Advert not found")

    repo.session.close()
    return {'detail': 'Advert deleted successfully.'}
