from fastapi import FastAPI

from migrations import run_migrations
from service.api.advertisement.endpoints import advert_router
from service.api.authorization.endpoints import auth_router
from settings import DEFAULT_DATABASE

app = FastAPI()

app.include_router(auth_router, prefix="/api/authorization")
app.include_router(advert_router, prefix="/api/advert")


@app.on_event("startup")
async def startup_event():
    run_migrations(DEFAULT_DATABASE)
