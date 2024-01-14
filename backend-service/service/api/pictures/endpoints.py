import uuid
import settings

from fastapi import UploadFile, File, APIRouter, HTTPException

picture_router = APIRouter()


@picture_router.post("/upload")
def upload(file: UploadFile = File(...)):
    try:
        contents = file.file.read()
        picture_uuid = uuid.uuid4()
        path = settings.PICTURE_FOLDER + str(picture_uuid)
        link = settings.PICTURE_PATH + str(picture_uuid)
        with open(path, 'wb') as f:
            f.write(contents)
    except Exception:
        raise HTTPException(status_code=500, detail="There was an error uploading the file")
    finally:
        file.file.close()

    return {"link": link}
