from fastapi import APIRouter
from app.services.preprocessing_service import PreprocessingService

router = APIRouter()
service = PreprocessingService()


@router.post("/preprocessing/run")
def run_preprocessing():
    output = service.run()
    return {
        "status": "SUCCESS",
        "output_file": output
    }
