import timestamp
from sqlalchemy import ForeignKey, create_engine, Engine,Null
from sqlalchemy import String
from sqlalchemy.orm import DeclarativeBase, Session
from sqlalchemy.orm import Mapped
from sqlalchemy.orm import mapped_column
from sqlalchemy import types
import enum


class Base(DeclarativeBase):
    pass


class Cat(enum.Enum):
    LOST = 'lost'
    FOUND = 'found'
    ABANDONED = 'abandoned'
    SHELTERED = 'sheltered'
    DEAD = 'dead'


class UserCustom(Base):
    __tablename__ = "user_custom"

    id: Mapped[int] = mapped_column(types.Integer, primary_key=True)
    username: Mapped[str] = mapped_column(String(50), nullable=False, unique=True)
    is_shelter: Mapped[bool] = mapped_column(types.Boolean, default=False)
    first_name: Mapped[str] = mapped_column(String(50), default=Null)
    last_name: Mapped[str] = mapped_column(String(50), default=Null)
    shelter_name: Mapped[str] = mapped_column(String(100), default=Null)
    email: Mapped[str] = mapped_column(String, nullable=False)
    phone_number: Mapped[str] = mapped_column(String(15), nullable=False)

    def __repr__(self) -> str:
        return (f"UserCustom(id={self.id!r}, name={self.username!r}, is_shelter={self.is_shelter!r}, "
                f"first_name={self.first_name!r}, last_name={self.last_name!r}, shelter_name={self.shelter_name!r}, "
                f"email={self.email!r}, phone_number={self.phone_number!r})")


class UserAuth(Base):
    __tablename__ = "user_auth"

    username: Mapped[str] = mapped_column(ForeignKey("user_custom.id"), primary_key=True)
    password: Mapped[str] = mapped_column(String(20), nullable=False)

    def __repr__(self) -> str:
        return f"UserAuth(username={self.username!r}, password={self.password!r})"


class Pet(Base):
    __tablename__ = "pet"

    id: Mapped[int] = mapped_column(types.Integer, primary_key=True)
    species: Mapped[str] = mapped_column(String(100), nullable=True)
    name: Mapped[str] = mapped_column(String(100), nullable=True)
    color: Mapped[str] = mapped_column(String(100), nullable=True)
    age: Mapped[int] = mapped_column(types.Integer, nullable=True)
    date_time_lost: Mapped[timestamp] = mapped_column(types.TIMESTAMP, nullable=True)
    location_lost: Mapped[str] = mapped_column(String(100), nullable=True)
    description: Mapped[str] = mapped_column(String(256), nullable=True)

    def __repr__(self) -> str:
        return (f"Pet(id={self.id!r}, species={self.species!r}, name={self.name!r}, "
                f"color={self.color!r}, age={self.age!r}, date_time_lost={self.date_time_lost!r}, "
                f"location_lost={self.location_lost!r}, description={self.description!r})")


class Advertisement(Base):
    __tablename__ = "advertisement"

    id: Mapped[int] = mapped_column(types.Integer, primary_key=True)
    category: Mapped[Cat] = mapped_column(types.Enum, nullable=False)
    deleted: Mapped[bool] = mapped_column(types.Boolean, default=False)
    user_id: Mapped[int] = mapped_column(ForeignKey("user_custom.id"))
    pet_id: Mapped[int] = mapped_column(ForeignKey("pet.id"))
    date_time_adv: Mapped[timestamp] = mapped_column(types.TIMESTAMP, nullable=False)
    is_in_shelter: Mapped[bool] = mapped_column(types.Boolean, default=False)
    shelter_id: Mapped[int] = mapped_column(types.Integer, nullable=True)

    def __repr__(self) -> str:
        return (f"Advertisement(id={self.id!r}, category={self.category!r}, deleted={self.deleted!r}, "
                f"user_id={self.user_id!r}, pet_id={self.pet_id!r}, date_time_adv={self.date_time_adv!r}, "
                f"is_in_shelter={self.is_in_shelter!r}, shelter_id={self.shelter_id!r})")


class Message(Base):
    __tablename__ = "message"

    id: Mapped[int] = mapped_column(types.Integer, primary_key=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("user_custom.id"))
    advert_id: Mapped[int] = mapped_column(ForeignKey("advertisement.id"))
    text: Mapped[str] = mapped_column(String, nullable=True)
    location: Mapped[str] = mapped_column(String, nullable=True)
    date_time_mess: Mapped[timestamp] = mapped_column(types.TIMESTAMP, nullable=False)

    def __repr__(self) -> str:
        return (f"Message(id={self.id!r}, user_id={self.user_id!r}, advert_id={self.advert_id!r}, "
                f"text={self.text!r}, location={self.location!r}, date_time_mess={self.date_time_mess!r})")

class Picture(Base):
    __tablename__ = "picture"

    id: Mapped[int] = mapped_column(types.Integer, primary_key=True)
    advert_id: Mapped[int] = mapped_column(ForeignKey("advertisement.id"))
    message_id: Mapped[int] = mapped_column(ForeignKey("message.id"))
    link: Mapped[str] = mapped_column(String, nullable=False)

    def __repr__(self) -> str:
        return f"Picture(id={self.id!r}, advert_id={self.advert_id!r}, message_id={self.message_id!r}, link={self.link!r})"


# engine = "postgresql://localhost:5432/postgres:postgres/baza"
# Base.metadata.create_all()