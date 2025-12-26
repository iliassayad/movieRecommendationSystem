import os
from pyspark.sql import SparkSession
from pyspark.ml.recommendation import ALS

INPUT_CSV = os.environ.get("INPUT_CSV", "/app/input/ratings_als.csv")
MODEL_PATH = os.environ.get("MODEL_PATH", "/app/model/als_model")

class ALSTrainingService:
    def __init__(self):
        self.spark = SparkSession.builder \
            .appName("ALSTrainingService") \
            .getOrCreate()
        self.spark.sparkContext.setLogLevel("WARN")

    def train_model(self):
        if not os.path.exists(INPUT_CSV):
            raise FileNotFoundError(f"{INPUT_CSV} not found")

        # Lecture du CSV
        ratings = self.spark.read.csv(INPUT_CSV, header=True, inferSchema=True)

        # Création et entraînement du modèle ALS
        als = ALS(userCol="userId", itemCol="movieId", ratingCol="rating",
                  coldStartStrategy="drop", nonnegative=True)
        model = als.fit(ratings)

        # Sauvegarde du modèle
        model.write().overwrite().save(MODEL_PATH)
        print(f"Model saved successfully to {MODEL_PATH}")
        return MODEL_PATH
