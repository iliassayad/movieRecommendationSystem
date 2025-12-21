from fastapi import FastAPI
from pyspark.sql import SparkSession
from pyspark.ml.recommendation import ALSModel
import os
import time

app = FastAPI()

spark = None
model = None
MODEL_PATH = "/model/latest"

def load_model():
    while not os.path.exists(MODEL_PATH):
        print("⏳ Waiting for model...")
        time.sleep(5)
    print("✅ Loading ALS model")
    return ALSModel.load(MODEL_PATH)

@app.on_event("startup")
def startup():
    global spark, model
    spark = (
        SparkSession.builder
        .appName("ALS-Recommendation-Service")
        .config("spark.driver.memory", "2g")
        .getOrCreate()
    )
    model = load_model()

@app.get("/recommend/{user_id}")
def recommend(user_id: int, k: int = 10):
    users = spark.createDataFrame([(user_id,)], ["userId"])
    recs = model.recommendForUserSubset(users, k).collect()

    if not recs or not recs[0]["recommendations"]:
        return {"userId": user_id, "recommendations": []}

    return {
        "userId": user_id,
        "recommendations": [
            {"movieId": r.movieId, "rating": r.rating}
            for r in recs[0]["recommendations"]
        ]
    }
