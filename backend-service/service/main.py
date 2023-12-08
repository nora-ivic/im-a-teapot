import json

from fastapi import FastAPI, Request, Response
from fastapi.exceptions import RequestValidationError, HTTPException
from starlette.responses import JSONResponse

from service.migrations import run_migrations
from service.init_populate import init_populate
from service.api.advertisement.endpoints import advert_router
from service.api.authorization.endpoints import auth_router
from settings import DEFAULT_DATABASE

app = FastAPI()

app.include_router(auth_router, prefix="/api/authorization")
app.include_router(advert_router, prefix="/api/advert")


@app.on_event("startup")
async def startup_event():
    run_migrations(DEFAULT_DATABASE)
    init_populate(DEFAULT_DATABASE)


@app.exception_handler(RequestValidationError)
def validation_exception_handler(request: Request, exc: RequestValidationError):
    return JSONResponse(status_code=400, content={"detail": str(exc)})
