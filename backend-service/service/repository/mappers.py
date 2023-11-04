from sqlalchemy.orm import DeclarativeBase


class Base(DeclarativeBase):
    pass


class UserAuth(Base):
    __tablename__ = "user_auth"


class UserCustom(Base):
    __tablename__ = "user_custom"


class Advertisement(Base):
    __tablename__ = "advertisement"


class Pet(Base):
    __tablename__ = "pet"


class Message(Base):
    __tablename__ = "message"

class Picture(Base):
    __tablename__ = "picture"

