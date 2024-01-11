from typing import List, Optional

from pydantic import BaseModel


class MessageInput(BaseModel):
    text: Optional[str] = None
    location: Optional[str] = None
    picture_links: List[str] = []


class MessageOutput(BaseModel):
    username: str
    text: Optional[str] = None
    location: Optional[str] = None
    picture_links: List[str] = []
