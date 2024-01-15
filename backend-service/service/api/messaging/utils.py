from typing import List

from fastapi import HTTPException

from service.api.messaging.models import MessageInput, MessageOutput
from service.repository.mappers import Message, UserCustom, Picture


def validate_message(input_message: MessageInput):
    if not input_message.text and not input_message.location and not input_message.picture_links:
        raise HTTPException(status_code=400, detail="Cannot send a message with neither text, location nor pictures")


def map_to_output_message(db_message: Message):
    user: UserCustom = db_message.user_sent
    pictures: List[Picture] = db_message.picture_posted

    return MessageOutput(
        username=user.username,
        user_email=user.email,
        user_phone_number=user.phone_number,
        text=db_message.text,
        location=db_message.location,
        picture_links=[picture.link for picture in pictures]
    )
