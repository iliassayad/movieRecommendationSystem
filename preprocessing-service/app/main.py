from fastapi import FastAPI
from app.api.preprocessing_routes import router

app = FastAPI(
    title="Preprocessing Service",
    description="Data preprocessing for ALS recommendation model",
    version="1.0.0"
)

app.include_router(router)
