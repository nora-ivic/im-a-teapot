from dotenv import load_dotenv
import os

load_dotenv()


APP_NAME = "Nestali ljubimci"
APP_VERSION = "0.1.1"

POSTGRES_USER = os.getenv('POSTGRES_USER')
POSTGRES_PASSWORD = os.getenv('POSTGRES_PASSWORD')
POSTGRES_HOST = os.getenv('POSTGRES_HOST')
POSTGRES_PORT = os.getenv('POSTGRES_PORT')

DEFAULT_DATABASE = "lost_pets"
TEST_DATABASE = "lost_pets_test"

SECRET_KEY = os.getenv('SECRET_KEY')
ALGORITHM = "HS256"
TOKEN_EXPIRE_MINUTES = 120
