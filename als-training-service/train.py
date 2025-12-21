from pyspark.sql import SparkSession
from pyspark.ml.recommendation import ALS
from pyspark.ml.evaluation import RegressionEvaluator
from datetime import datetime
import os
from pyspark.sql.functions import col
from pyspark.sql.types import IntegerType, FloatType


spark = SparkSession.builder \
    .appName("Weekly-ALS-Training") \
    .getOrCreate()

print("🚀 Starting ALS training job")

ratings_path = "/datasets/ratings.csv"

# ratings = spark.read.csv(
#     ratings_path,
#     header=True,
#     inferSchema=True
# ).select("userId", "movieId", "rating")
ratings = spark.read.csv(
    ratings_path,
    header=True,
    inferSchema=True
).select(
    col("userId").cast(IntegerType()),
    col("movieId").cast(IntegerType()),
    col("rating").cast(FloatType())
)
train, test = ratings.randomSplit([0.8, 0.2], seed=42)

als = ALS(
    userCol="userId",
    itemCol="movieId",
    ratingCol="rating",
    coldStartStrategy="drop",
    nonnegative=True,
    rank=50,
    maxIter=15,
    regParam=0.1
)

model = als.fit(train)

predictions = model.transform(test)

evaluator = RegressionEvaluator(
    metricName="rmse",
    labelCol="rating",
    predictionCol="prediction"
)

rmse = evaluator.evaluate(predictions)
print(f"📉 RMSE: {rmse}")

# ---- Model versioning ----
timestamp = datetime.utcnow().strftime("%Y%m%d_%H%M%S")
model_path = f"/model/als_model_{timestamp}"

model.write().overwrite().save(model_path)

# Update "latest" symlink
latest_path = "/model/latest"
if os.path.exists(latest_path):
    os.remove(latest_path)
os.symlink(model_path, latest_path)

print(f"✅ Model saved to {model_path}")
print("🏁 Training completed")

spark.stop()
