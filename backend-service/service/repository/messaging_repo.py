from sqlalchemy.orm import Session, joinedload
from typing import Optional
from datetime import datetime

from service.api.messaging.models import MessageInput
from service.repository.engine_manager import get_session
from service.repository.mappers import Message, Picture


class MessagingRepository:
    def __init__(self, session: Optional[Session] = None):
        self.session = session if session else get_session()

    def post_message(
            self,
            advert_id: int,
            user_id: int,
            message_input: MessageInput
    ):
        new_message = Message(
            user_id=user_id,
            advert_id=advert_id,
            text=message_input.text,
            location=message_input.location,
            date_time_mess=datetime.now()
        )

        self.session.add(new_message)
        self.session.flush()

        for link in message_input.picture_links:
            link = link.lstrip('https://lost-pets-progi-backend-2023-2024.onrender.com')
            new_picture = Picture(
                message_id=new_message.id,
                link=link
            )
            self.session.add(new_picture)

        self.session.commit()

    def get_messages(
            self,
            advert_id: int,
            page: int,
            page_size: int
    ):
        query = (
            self.session.query(Message)
            .options(joinedload(Message.user_sent), joinedload(Message.advert_posted))
            .filter(Message.advert_id == advert_id)
        )

        return (
            query
            .order_by(Message.date_time_mess)
            .limit(page_size).offset((page - 1) * page_size)
            .all()
        )
