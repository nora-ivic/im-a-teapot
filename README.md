# im-a-teapot
PROGI - 2023/24 - I'm a teapot

### ANDROID APP
- poveznica za preuzimanje aplikacije: `https://www.amazon.com/gp/product/B0CLKBFN9N`

### WEB SERVER
- poveznica na Swagger dokumentaciju backenda: `https://lost-pets-progi-backend-2023-2024.onrender.com/docs`

##
### LOKALNO POKRETANJE BACKENDA
- lokalno napraviti bazu `lost_pets`
- u folderu `backend-service` napraviti virtual environment `venv`
- u folderu `backend-service` pokrenuti:
```commandline
pip install -r requirements.txt
uvicorn service.main:app --reload
```
