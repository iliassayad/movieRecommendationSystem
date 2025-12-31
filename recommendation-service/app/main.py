from fastapi import FastAPI
from app.eureka_client import EurekaClient
from pydantic import BaseModel
from app.services.als_recommendation_service import ALSRecommendationService


eureka_client = EurekaClient(
    eureka_server="http://discovery-service:8761/eureka/",
    app_name="recommendation-service",
    instance_port=8000,
)

app = FastAPI(title="Recommendation Service")

@app.on_event("startup")
async def startup_event():
    await eureka_client.start() 
    

@app.on_event("shutdown")
async def shutdown_event():
    await eureka_client.stop()


als_service = ALSRecommendationService()

class UserRequest(BaseModel):
    userId: int
    topN: int = 10

@app.post("/recommendations")
def recommend(request: UserRequest):
    recommendations = als_service.recommend_for_user(request.userId, request.topN)
    return recommendations

