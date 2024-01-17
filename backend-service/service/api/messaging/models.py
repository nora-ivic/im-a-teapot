from typing import List, Optional

from pydantic import BaseModel


class MessageInput(BaseModel):
    text: Optional[str] = None
    location: Optional[str] = None
    picture_links: List[str] = []


class MessageOutput(BaseModel):
    username: str
    user_email: str
    user_phone_number: str
    text: Optional[str] = None
    location: Optional[str] = None
    picture_links: List[str] = []
