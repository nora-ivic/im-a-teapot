from sqlalchemy import desc
from sqlalchemy.orm import Session, joinedload
from typing import Optional

from service.api.advertisement.filters import AdvertisementFilter
from service.repository.engine_manager import get_session
from service.repository.mappers import Advertisement
from service.enums import AdvertisementCategory


class AdvertisementRepository:
    def __init__(self, session: Optional[Session] = None):
        self.session = session if session else get_session()

    def _filter_query(self, query):
        return query

    def get_adverts(
            self,
            user_id: int,
            page: int,
            page_size: int,
            filter: AdvertisementFilter = None,
    ):
        query = (self.session
                 .query(
                        Advertisement
                        )
                 .options(joinedload(Advertisement.user_posted), joinedload(Advertisement.pet_posted))
                 )
        query = self._filter_query(query)

        if not user_id:
            query = query.filter(Advertisement.category == AdvertisementCategory.LOST.value)

        return (query
                .order_by(desc(Advertisement.date_time_adv))
                .limit(page_size).offset((page - 1) * page_size)
                .all()
                )
