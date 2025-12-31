from fastapi import FastAPI
from app.eureka_client import EurekaClient
from app.services.als_training_service import ALSTrainingService

eureka_client = EurekaClient(
    eureka_server="http://discovery-service:8761/eureka/",
    app_name="training-service",
    instance_port=8000,
)

app = FastAPI()
training_service = ALSTrainingService()

@app.on_event("startup")
async def startup_event():
    await eureka_client.start()
    
@app.on_event("shutdown")
async def shutdown_event():
    await eureka_client.stop()
    

@app.post("/training")
def train():
    model_path = training_service.train_model()
    return {"status": "success", "model_path": model_path}
