from typing import List
from sqlalchemy import ForeignKey, create_engine
from sqlalchemy import String, DateTime
from sqlalchemy.orm import DeclarativeBase, relationship
from sqlalchemy.orm import Mapped
from sqlalchemy.orm import mapped_column
from sqlalchemy import types
from datetime import datetime
from service.enums import AdvertisementCategory


class Base(DeclarativeBase):
    pass


class UserCustom(Base):
    __tablename__ = "user_custom"

    id: Mapped[int] = mapped_column(types.Integer, primary_key=True)
    username: Mapped[str] = mapped_column(String(50), nullable=False, unique=True)
    is_shelter: Mapped[bool] = mapped_column(types.Boolean, insert_default=False)
    first_name: Mapped[str] = mapped_column(String(50), nullable=True)
    last_name: Mapped[str] = mapped_column(String(50), nullable=True)
    shelter_name: Mapped[str] = mapped_column(String(100), nullable=True)
    email: Mapped[str] = mapped_column(String, nullable=False)
    phone_number: Mapped[str] = mapped_column(String(15), nullable=False)

    login_info: Mapped["UserAuth"] = relationship(back_populates="user_info")
    messages_sent: Mapped[List["Message"]] = relationship(back_populates="user_sent")
    adverts_posted: Mapped[List["Advertisement"]] = relationship(back_populates="user_posted")

    def __repr__(self) -> str:
        return (f"UserCustom(id={self.id!r}, username={self.username!r}, is_shelter={self.is_shelter!r}, "
                f"first_name={self.first_name!r}, last_name={self.last_name!r}, shelter_name={self.shelter_name!r}, "
                f"email={self.email!r}, phone_number={self.phone_number!r})")


class UserAuth(Base):
    __tablename__ = "user_auth"

    username: Mapped[str] = mapped_column(ForeignKey("user_custom.id"), primary_key=True)
    password: Mapped[str] = mapped_column(String(128), nullable=False)

    user_info: Mapped["UserCustom"] = relationship(back_populates="login_info")

    def __repr__(self) -> str:
        return f"UserAuth(username={self.username!r})"


class Pet(Base):
    __tablename__ = "pet"

    id: Mapped[int] = mapped_column(types.Integer, primary_key=True)
    species: Mapped[str] = mapped_column(String(100), nullable=True)
    name: Mapped[str] = mapped_column(String(100), nullable=True)
    color: Mapped[str] = mapped_column(String(100), nullable=True)
    age: Mapped[int] = mapped_column(types.Integer, nullable=True)
    date_time_lost: Mapped[datetime] = mapped_column(DateTime(timezone=False), nullable=True)
    location_lost: Mapped[str] = mapped_column(String(256), nullable=True)
    description: Mapped[str] = mapped_column(String, nullable=True)

    advert_posted: Mapped[List["Advertisement"]] = relationship(back_populates="pet_posted")

    def __repr__(self) -> str:
        return (f"Pet(id={self.id!r}, species={self.species!r}, name={self.name!r}, "
                f"color={self.color!r}, age={self.age!r}, date_time_lost={self.date_time_lost!r}, "
                f"location_lost={self.location_lost!r}, description={self.description!r})")


class Advertisement(Base):
    __tablename__ = "advertisement"

    id: Mapped[int] = mapped_column(types.Integer, primary_key=True)
    category: Mapped[AdvertisementCategory] = mapped_column(types.Enum('lost', 'found', 'abandoned', 'sheltered', 'dead', name="category"), insert_default=AdvertisementCategory.LOST)
    deleted: Mapped[bool] = mapped_column(types.Boolean, insert_default=False)
    user_id: Mapped[int] = mapped_column(ForeignKey("user_custom.id"), nullable=True)
    pet_id: Mapped[int] = mapped_column(ForeignKey("pet.id"), nullable=False)
    date_time_adv: Mapped[datetime] = mapped_column(DateTime(timezone=False), insert_default=datetime.now())
    is_in_shelter: Mapped[bool] = mapped_column(types.Boolean, insert_default=False)
    shelter_id: Mapped[int] = mapped_column(types.Integer, nullable=True)

    pet_posted: Mapped["Pet"] = relationship(back_populates="advert_posted")
    picture_posted: Mapped[List["Picture"]] = relationship(back_populates="advert_posted")
    communication: Mapped[List["Message"]] = relationship(back_populates="advert_posted")
    user_posted: Mapped["UserCustom"] = relationship(back_populates="adverts_posted")

    def __repr__(self) -> str:
        return (f"Advertisement(id={self.id!r}, category={self.category!r}, deleted={self.deleted!r}, "
                f"user_id={self.user_id!r}, pet_id={self.pet_id!r}, date_time_adv={self.date_time_adv!r}, "
                f"is_in_shelter={self.is_in_shelter!r}, shelter_id={self.shelter_id!r})")


class Message(Base):
    __tablename__ = "message"

    id: Mapped[int] = mapped_column(types.Integer, primary_key=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("user_custom.id"), nullable=False)
    advert_id: Mapped[int] = mapped_column(ForeignKey("advertisement.id"), nullable=False)
    text: Mapped[str] = mapped_column(String, nullable=True)
    location: Mapped[str] = mapped_column(String(256), nullable=True)
    date_time_mess: Mapped[datetime] = mapped_column(DateTime(timezone=False), insert_default=datetime.now())

    picture_posted: Mapped[List["Picture"]] = relationship(back_populates="message_posted")
    user_sent: Mapped["UserCustom"] = relationship(back_populates="message_sent")
    advert_posted: Mapped["Advertisement"] = relationship(back_populates="communication")

    def __repr__(self) -> str:
        return (f"Message(id={self.id!r}, user_id={self.user_id!r}, advert_id={self.advert_id!r}, "
                f"text={self.text!r}, location={self.location!r}, date_time_mess={self.date_time_mess!r})")


class Picture(Base):
    __tablename__ = "picture"

    id: Mapped[int] = mapped_column(types.Integer, primary_key=True)
    advert_id: Mapped[int] = mapped_column(ForeignKey("advertisement.id"), nullable=True)
    message_id: Mapped[int] = mapped_column(ForeignKey("message.id"), nullable=True)
    link: Mapped[str] = mapped_column(String(256), nullable=False)

    advert_posted: Mapped["Advertisement"] = relationship(back_populates="picture_posted")
    message_posted: Mapped["Message"] = relationship(back_populates="picture_posted")

    def __repr__(self) -> str:
        return (f"Picture(id={self.id!r}, advert_id={self.advert_id!r}, "
                f"message_id={self.message_id!r}, link={self.link!r})")
