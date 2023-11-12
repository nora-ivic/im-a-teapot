import enum


class AdvertisementCategory(enum.Enum):
    LOST = 'lost'
    FOUND = 'found'
    ABANDONED = 'abandoned'
    SHELTERED = 'sheltered'
    DEAD = 'dead'
