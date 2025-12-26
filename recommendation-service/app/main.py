from fastapi import FastAPI
from pydantic import BaseModel
from app.services.als_recommendation_service import ALSRecommendationService

app = FastAPI(title="Recommendation Service")

als_service = ALSRecommendationService()

class UserRequest(BaseModel):
    userId: int
    topN: int = 10

@app.post("/recommend")
def recommend(request: UserRequest):
    recommendations = als_service.recommend_for_user(request.userId, request.topN)
    return {"userId": request.userId, "recommendations": recommendations}

@app.get("/health")
def health():
    return {"status": "ok"}
