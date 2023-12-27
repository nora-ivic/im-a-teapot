from sqlalchemy import Date
from sqlalchemy import desc
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session, joinedload
from typing import Optional
from datetime import date

from service.api.advertisement.filters import AdvertisementFilter
from service.api.advertisement.models import AdvertisementInput
from service.exceptions import PermissionDeniedException, AdvertNotFoundException
from service.repository.engine_manager import get_session
from service.repository.mappers import Advertisement, Pet, UserCustom
from service.enums import AdvertisementCategory


class AdvertisementRepository:
    def __init__(self, session: Optional[Session] = None):
        self.session = session if session else get_session()

    def _filter_query(self, query, filter_):
        if filter_.advert_category:
            query = query.filter(Advertisement.category == filter_.advert_category.value)
        if filter_.pet_name:
            query = query.filter(Pet.name.ilike(filter_.pet_name))
        if filter_.pet_species:
            query = query.filter(Pet.species == filter_.pet_species.value)
        if filter_.pet_color:
            query = query.filter(Pet.color.ilike(filter_.pet_color))
        if filter_.pet_age:
            query = query.filter(Pet.age == filter_.pet_age)
        if filter_.date_time_lost:
            query = query.filter(Pet.date_time_lost.cast(Date) == filter_.date_time_lost)
        if filter_.location_lost:
            # TODO maybe filter by location
            pass
        if filter_.description:
            query = query.filter(Pet.description.icontains(filter_.description))
        if filter_.is_in_shelter is not None:
            query = query.filter(Advertisement.is_in_shelter == filter_.is_in_shelter)
        if filter_.username:
            query = query.filter(UserCustom.username.ilike(filter_.username))
        if filter_.shelter_name:
            query = query.filter(UserCustom.shelter_name.ilike(filter_.shelter_name))

        return query

    def _save_advert(self, advert: Advertisement = None, pet: Pet = None):
        if advert:
            self.session.add(advert)
        if pet:
            self.session.add(pet)
        self.session.commit()


    def get_adverts(
            self,
            user_id: int,
            page: int,
            page_size: int,
            filter_: AdvertisementFilter,
    ):
        query = (
            self.session.query(Advertisement)
            .options(
                joinedload(Advertisement.user_posted),
                joinedload(Advertisement.pet_posted),
                joinedload(Advertisement.shelter)
            )
            .filter(Advertisement.deleted == False)
        )
        if filter_:
            query = self._filter_query(query, filter_)

        if not user_id:
            query = query.filter(Advertisement.category == AdvertisementCategory.LOST.value)

        return (
            query
            .order_by(desc(Advertisement.date_time_adv))
            .limit(page_size).offset((page - 1) * page_size)
            .all()
        )

    def get_advert_by_id(self, advert_id: int):
        query = (
            self.session.query(Advertisement)
            .options(
                joinedload(Advertisement.user_posted),
                joinedload(Advertisement.pet_posted),
                joinedload(Advertisement.shelter)
            )
            .filter(Advertisement.id == advert_id)
        )

        return (
            query.first()
        )

    def create_advert(self, advert_input: AdvertisementInput, user_id: int):
        new_pet = Pet(
            species=advert_input.pet_species,
            name=advert_input.pet_name,
            color=advert_input.pet_color,
            age=advert_input.pet_age,
            date_time_lost=advert_input.date_time_lost,
            location_lost=advert_input.location_lost,
            description=advert_input.description,
        )
        self.session.add(new_pet)
        self.session.flush()

        new_advert = Advertisement(
            category=advert_input.advert_category,
            deleted=False,
            user_id=user_id,
            pet_id=new_pet.id,
            is_in_shelter=advert_input.is_in_shelter,
        )

        self._save_advert(new_advert, new_pet)
        return new_advert

    def edit_advert(self, advert_input: AdvertisementInput, advert_id: int, user_id: int):
        advert = (
            self.session.query(Advertisement)
            .options(joinedload(Advertisement.pet_posted))
            .filter(Advertisement.id == advert_id).first()
        )
        if not advert:
            raise AdvertNotFoundException

        if advert.user_id != user_id:
            raise PermissionDeniedException

        pet = advert.pet_posted

        # TODO editing

        self._save_advert(advert, pet)
        return advert

    def delete_advert(self, advert_id: int, user_id: int):
        advert = self.session.query(Advertisement).filter(Advertisement.id == advert_id).first()
        if not advert:
            raise AdvertNotFoundException
        if advert.user_id != user_id:
            raise PermissionDeniedException

        advert.deleted = True
        self._save_advert(advert)
