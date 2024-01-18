from sqlalchemy import Date
from sqlalchemy import desc, select
from sqlalchemy.orm import Session, joinedload
from typing import Optional
from datetime import datetime

from service.api.advertisement.filters import AdvertisementFilter
from service.api.advertisement.models import AdvertisementInput
from service.exceptions import PermissionDeniedException, AdvertNotFoundException
from service.repository.authorization_repo import AuthorizationRepository
from service.repository.engine_manager import get_session
from service.repository.mappers import Advertisement, Pet, UserCustom, Picture
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
            shelter_id = self.session.scalar(
                select(UserCustom.id).where(UserCustom.shelter_name.ilike(filter_.shelter_name))
            )
            if shelter_id:
                query = query.filter(Advertisement.shelter_id == shelter_id)
            else:
                raise AdvertNotFoundException
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
            .join(Advertisement.user_posted)
            .join(Advertisement.pet_posted)
            .filter(Advertisement.deleted == False)
        )
        if filter_:
            try:
                query = self._filter_query(query, filter_)
            except AdvertNotFoundException:
                return []

        if not user_id:
            query = query.filter(Advertisement.category == AdvertisementCategory.LOST.value)

        return (
            query
            .order_by(desc(Advertisement.date_time_adv))
            .limit(page_size).offset((page - 1) * page_size)
            .all()
        )

    def get_user_adverts(
            self,
            user_id: int,
            page: int,
            page_size: int
    ):
        query = (
            self.session.query(Advertisement)
            .options(
                joinedload(Advertisement.user_posted),
                joinedload(Advertisement.pet_posted),
                joinedload(Advertisement.shelter)
            )
            .filter(Advertisement.deleted == False, Advertisement.user_id == user_id)
        )

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

    def is_shelter(self, user_id: int):
        auth_repo = AuthorizationRepository(session=self.session)
        is_shelter = auth_repo.check_is_shelter(user_id=user_id)
        return is_shelter

    def make_sheltered(
            self,
            advert_id: int,
            user_id: int
    ):
        advert = (
            self.session.query(Advertisement)
            .filter(Advertisement.id == advert_id)
            .first()
        )

        if not advert:
            raise AdvertNotFoundException

        advert.category = 'sheltered'
        advert.is_in_shelter = True
        advert.shelter_id = user_id

        self._save_advert(advert)
        return advert

    def create_advert(self, advert_input: AdvertisementInput, user_id: int):
        new_pet = Pet(
            species=advert_input.pet_species.value if advert_input.pet_species else None,
            name=advert_input.pet_name,
            color=advert_input.pet_color,
            age=advert_input.pet_age,
            date_time_lost=advert_input.date_time_lost,
            location_lost=advert_input.location_lost,
            description=advert_input.description,
        )
        self.session.add(new_pet)
        self.session.flush()

        auth_repo = AuthorizationRepository(session=self.session)
        is_shelter = auth_repo.check_is_shelter(user_id=user_id)
        category = (
            AdvertisementCategory.SHELTERED.value
            if is_shelter and advert_input.advert_category.value == AdvertisementCategory.SHELTERED.value
            else AdvertisementCategory.LOST.value
        )

        new_advert = Advertisement(
            category=category,
            deleted=False,
            user_id=user_id,
            pet_id=new_pet.id,
            is_in_shelter=True if category == AdvertisementCategory.SHELTERED.value else False,
            shelter_id=(
                user_id
                if category == AdvertisementCategory.SHELTERED.value
                else None
            ),
            date_time_adv=datetime.now()
        )
        self.session.add(new_advert)
        self.session.flush()

        for picture_link in advert_input.picture_links:
            new_picture = Picture(
                advert_id=new_advert.id,
                link=picture_link,
            )
            self.session.add(new_picture)

        self._save_advert(new_advert, new_pet)
        return new_advert

    def edit_advert(self, advert_input: AdvertisementInput, advert_id: int, user_id: int):
        advert = (
            self.session.query(Advertisement)
            .options(joinedload(Advertisement.user_posted))
            .filter(Advertisement.id == advert_id).first()
        )

        if not advert:
            raise AdvertNotFoundException

        if advert.user_id != user_id:
            raise PermissionDeniedException

        pet = self.session.query(Pet).filter(Pet.id == advert.pet_id).first()
        user = advert.user_posted

        pet.species = advert_input.pet_species.value if advert_input.pet_species else None
        pet.name = advert_input.pet_name
        pet.color = advert_input.pet_color
        pet.age = advert_input.pet_age
        pet.date_time_lost = advert_input.date_time_lost
        pet.location_lost = advert_input.location_lost
        pet.description = advert_input.description

        advert.category = advert_input.advert_category.value
        advert.is_in_shelter = True if user.is_shelter and advert_input.is_in_shelter else False
        advert.shelter_id = user.id if user.is_shelter and advert_input.is_in_shelter else None

        for picture_link in advert_input.picture_links:
            existing = self.session.query(Picture).filter(Picture.link == picture_link).first()
            if existing:
                continue
            else:
                new_picture = Picture(advert_id=advert.id, link=picture_link)
                self.session.add(new_picture)

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
