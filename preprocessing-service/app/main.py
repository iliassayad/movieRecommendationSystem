from fastapi import FastAPI
from app.api.preprocessing_routes import router
from app.eureka_client import EurekaClient
eureka_client = EurekaClient(
    eureka_server="http://discovery-service:8761/eureka/",
    app_name="preprocessing-service",
    instance_port=8000,
)

app = FastAPI(
    title="Preprocessing Service",
    description="Data preprocessing for ALS recommendation model",
    version="1.0.0"
)

@app.on_event("startup")
async def startup_event():
    await eureka_client.start()
    
@app.on_event("shutdown")
async def shutdown_event():
    await eureka_client.stop()

app.include_router(router)
