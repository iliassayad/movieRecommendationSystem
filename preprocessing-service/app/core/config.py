# app/core/config.py
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    MONGODB_URI: str = "mongodb://iliass:iliass@mongodb-ingestion:27017/ingestiondb?authSource=admin"
    MONGODB_DB: str = "ingestiondb"
    RATINGS_COLLECTION: str = "ratings"
    MOVIES_COLLECTION: str = "movies"
    OUTPUT_DIR: str = "output"
    OUTPUT_FILE: str = "ratings_als.csv"
    MIN_USER_INTERACTIONS: int = 5
    MIN_MOVIE_INTERACTIONS: int = 10

    class Config:
        env_file = ".env"  # si tu veux charger des variables depuis un fichier .env
        env_file_encoding = "utf-8"

settings = Settings()
