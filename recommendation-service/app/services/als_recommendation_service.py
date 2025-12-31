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
        
        if not recs:
            return {
                "userId": int(user_id),
                "recommendations": []
            }
        
        # Transformer la colonne 'recommendations' en liste de dictionnaires
        formatted_recommendations = [
            {
                "movieId": int(rec.movieId),
                "score": float(rec.rating)
            }
            for rec in recs[0].recommendations
        ]
        
        return {
            "userId": int(user_id),
            "recommendations": formatted_recommendations
        }
        
        # // Exemple de retour :
        # // {
        # //     "userId": 123,
        # //     "recommendations": [
        # //         {"movieId": 456, "score": 4.5},  
        # //         {"movieId": 789, "score": 4.0}
        # //     ]
        
        # // }    
