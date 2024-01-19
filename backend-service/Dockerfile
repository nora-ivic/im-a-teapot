FROM python:3.10

WORKDIR /src

COPY ./requirements.txt /src/requirements.txt
COPY ./settings.py /src/settings.py
COPY ./.env /src/.env
COPY ./nestali-ljubimci-633ce905492c.json /src/nestali-ljubimci-633ce905492c.json

RUN pip install --no-cache-dir --upgrade -r /src/requirements.txt

COPY ./service /src/service

CMD ["uvicorn", "service.main:app", "--host", "0.0.0.0", "--port", "8000"]
