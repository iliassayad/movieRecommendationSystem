import os
from pyspark.sql import SparkSession
from pyspark.ml.recommendation import ALSModel

class ALSRecommendationService:
    def __init__(self, model_path="/app/model/als_model"):
        self.spark = SparkSession.builder \
            .appName("ALS Recommendation Service") \
            .getOrCreate()
        self.model_path = model_path
        self.model = ALSModel.load(model_path)
        print(f"Model loaded from {model_path}")

    def recommend_for_user(self, user_id, num_recommendations=10):
        """
        Retourne une liste de recommandations pour un utilisateur donné
        """
        user_df = self.spark.createDataFrame([(user_id,)], ["userId"])
        recommendations = self.model.recommendForUserSubset(user_df, num_recommendations)
        recs = recommendations.collect()
        if recs:
            return [(row.movieId, row.rating) for row in recs[0].recommendations]
        return []
