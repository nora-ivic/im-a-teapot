from firebase_admin import credentials, initialize_app

from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from starlette.responses import JSONResponse

from service.api.messaging.endpoints import messages_router
from service.api.pictures.endpoints import picture_router
from service.migrations import run_migrations
from service.init_populate import init_populate
from service.api.advertisement.endpoints import advert_router
from service.api.authorization.endpoints import auth_router
from settings import DEFAULT_DATABASE, FIREBASE_CERTIFICATE, FIREBASE_PATH

app = FastAPI()

cred = credentials.Certificate(FIREBASE_CERTIFICATE)
initialize_app(cred, {'storageBucket': FIREBASE_PATH})

app.include_router(auth_router, prefix="/api/authorization")
app.include_router(advert_router, prefix="/api/advert")
app.include_router(messages_router, prefix="/api/messages")
app.include_router(picture_router, prefix="/api/pictures")


@app.on_event("startup")
async def startup_event():
    run_migrations(DEFAULT_DATABASE)
    init_populate(DEFAULT_DATABASE)


@app.exception_handler(RequestValidationError)
def validation_exception_handler(request: Request, exc: RequestValidationError):
    return JSONResponse(status_code=400, content={"detail": str(exc)})


@app.get("/health-check-status")
def health_check():
    return "I'm a teapot"
