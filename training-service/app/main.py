from fastapi import FastAPI
from app.services.als_training_service import ALSTrainingService

app = FastAPI()
training_service = ALSTrainingService()

@app.post("/train")
def train():
    model_path = training_service.train_model()
    return {"status": "success", "model_path": model_path}
