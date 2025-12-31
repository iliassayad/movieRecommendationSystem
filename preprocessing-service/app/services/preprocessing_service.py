import os
import pandas as pd
from pymongo import MongoClient
from app.core.config import settings
from app.core.logger import logger
from app.utils.csv_writer import write_csv


class PreprocessingService:

    def __init__(self):
        self.client = MongoClient(settings.MONGODB_URI)
        self.db = self.client[settings.MONGODB_DB]

    # ---------- LOAD ----------
    def load_ratings(self) -> pd.DataFrame:
        logger.info("Loading ratings from MongoDB")
        data = list(self.db[settings.RATINGS_COLLECTION].find({}, {"_id": 0}))
        return pd.DataFrame(data)

    def load_movies(self) -> pd.DataFrame:
        logger.info("Loading movies from MongoDB")
        data = list(
            self.db[settings.MOVIES_COLLECTION].find({}, {"_id": 0, "movieId": 1})
        )
        return pd.DataFrame(data)

    # ---------- CLEANING ----------
    def remove_null_ratings(self, df: pd.DataFrame) -> pd.DataFrame:
        logger.info("Removing null ratings")
        return df.dropna(subset=["userId", "movieId", "rating"])

    def remove_non_existing_movies(
        self, ratings_df: pd.DataFrame, movies_df: pd.DataFrame
    ) -> pd.DataFrame:
        logger.info("Removing ratings for non-existing movies")
        valid_movies = set(movies_df["movieId"].astype(int))
        return ratings_df[ratings_df["movieId"].isin(valid_movies)]

    def remove_duplicates(self, df: pd.DataFrame) -> pd.DataFrame:
        logger.info("Removing duplicate ratings")
        return df.drop_duplicates(subset=["userId", "movieId"])

    # ---------- TYPE VALIDATION ----------
    def validate_types(self, df: pd.DataFrame) -> pd.DataFrame:
        logger.info("Validating data types")
        df["userId"] = df["userId"].astype(int)
        df["movieId"] = df["movieId"].astype(int)
        df["rating"] = df["rating"].astype(float)
        return df

    # ---------- FILTERING ----------
    def filter_users(self, df: pd.DataFrame) -> pd.DataFrame:
        logger.info("Filtering users with few interactions")
        user_counts = df["userId"].value_counts()
        valid_users = user_counts[user_counts >= settings.MIN_USER_INTERACTIONS].index
        return df[df["userId"].isin(valid_users)]

    def filter_movies(self, df: pd.DataFrame) -> pd.DataFrame:
        logger.info("Filtering movies with few interactions")
        movie_counts = df["movieId"].value_counts()
        valid_movies = movie_counts[movie_counts >= settings.MIN_MOVIE_INTERACTIONS].index
        return df[df["movieId"].isin(valid_movies)]

    # ---------- FINAL FORMAT ----------
    def format_for_als(self, df: pd.DataFrame) -> pd.DataFrame:
        logger.info("Formatting dataset for ALS")
        return df[["userId", "movieId", "rating"]]

    # ---------- EXPORT ----------
    def export_csv(self, df: pd.DataFrame) -> str:
        os.makedirs(settings.OUTPUT_DIR, exist_ok=True)
        output_path = os.path.join(settings.OUTPUT_DIR, settings.OUTPUT_FILE)
        write_csv(df, output_path)
        logger.info(f"CSV exported to {output_path}")
        return output_path

    # ---------- PIPELINE ----------
    def run(self) -> str:
        ratings = self.load_ratings()
        movies = self.load_movies()

        ratings = self.remove_null_ratings(ratings)
        ratings = self.remove_non_existing_movies(ratings, movies)
        ratings = self.remove_duplicates(ratings)
        ratings = self.validate_types(ratings)

        ratings = self.filter_users(ratings)
        ratings = self.filter_movies(ratings)

        ratings = self.format_for_als(ratings)

        return self.export_csv(ratings)
