import uuid

from firebase_admin import storage

import settings

from fastapi import UploadFile, File, APIRouter, HTTPException

picture_router = APIRouter()


@picture_router.post("/upload")
def upload(file: UploadFile = File(...)):
    try:
        contents = file.file.read()

        picture_uuid = uuid.uuid4()
        path = settings.PICTURE_FOLDER + str(picture_uuid)
        with open(path, 'wb') as f:
            f.write(contents)

        bucket = storage.bucket()
        blob = bucket.blob(path)
        blob.upload_from_filename(path)

        blob.make_public()
        link = blob.public_url

    except Exception:
        raise HTTPException(status_code=500, detail="There was an error uploading the file")
    finally:
        file.file.close()

    return {"link": link}
