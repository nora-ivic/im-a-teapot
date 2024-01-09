import enum


class AdvertisementCategory(enum.Enum):
    LOST = 'lost'
    FOUND = 'found'
    ABANDONED = 'abandoned'
    SHELTERED = 'sheltered'
    DEAD = 'dead'


class PetSpecies(enum.Enum):
    BIRD = 'bird'
    CAT = 'cat'
    DOG = 'dog'
    LIZARD = 'lizard'
    OTHER = 'other'
    RABBIT = 'rabbit'
    RODENT = 'rodent'
    SNAKE = 'snake'
