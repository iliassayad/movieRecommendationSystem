# app/main.py
import sys
import logging
import threading

# Configuration logging dès le début
sys.stdout.reconfigure(line_buffering=True)
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[logging.StreamHandler(sys.stdout)]
)
logger = logging.getLogger(__name__)

logger.info("=" * 80)
logger.info("🚀 INITIALISATION DE MAIN.PY")
logger.info("=" * 80)

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

# Variable globale pour le thread
kafka_thread = None

def start_kafka_thread():
    """Démarre le thread Kafka listener"""
    global kafka_thread
    
    logger.info("=" * 60)
    logger.info("🎯 Démarrage du thread Kafka listener")
    logger.info("=" * 60)
    
    if kafka_thread is not None and kafka_thread.is_alive():
        logger.warning("⚠️ Thread Kafka déjà actif")
        return False
    
    try:
        from app.kafka_listener import start_kafka_listener
        
        def safe_kafka_start():
            try:
                logger.info("🔥 Thread Kafka: exécution de start_kafka_listener()")
                sys.stdout.flush()
                start_kafka_listener()
            except Exception as e:
                logger.error(f"❌ Erreur dans le thread Kafka: {e}")
                import traceback
                traceback.print_exc()
                sys.stdout.flush()
        
        kafka_thread = threading.Thread(
            target=safe_kafka_start,
            daemon=True,
            name="KafkaListener"
        )
        kafka_thread.start()
        
        logger.info(f"✅ Thread Kafka lancé (alive: {kafka_thread.is_alive()})")
        
        # Attendre un peu pour vérifier que le thread démarre bien
        import time
        time.sleep(1)
        logger.info(f"🔍 Vérification après 1s: thread alive = {kafka_thread.is_alive()}")
        
        return True
        
    except Exception as e:
        logger.error(f"❌ Erreur lors du démarrage du thread Kafka: {e}")
        import traceback
        traceback.print_exc()
        return False

@app.on_event("startup")
async def startup_event():
    logger.info("=" * 80)
    logger.info("🔧 STARTUP EVENT - Démarrage des services")
    logger.info("=" * 80)
    
    # Démarrer Eureka
    await eureka_client.start()
    logger.info("✅ Eureka client démarré")
    
    # Démarrer le thread Kafka
    success = start_kafka_thread()
    logger.info(f"✅ Thread Kafka: {'démarré avec succès' if success else 'échec du démarrage'}")
    
    # Attendre et afficher l'état des threads
    import time
    time.sleep(2)
    
    logger.info("=" * 80)
    logger.info("📊 État final des threads:")
    for thread in threading.enumerate():
        logger.info(f"   - {thread.name}: alive={thread.is_alive()}, daemon={thread.daemon}")
    logger.info("=" * 80)

@app.on_event("shutdown")
async def shutdown_event():
    logger.info("🛑 Arrêt du service")
    await eureka_client.stop()

# Inclure les routes
app.include_router(router)

# Endpoint de santé pour vérifier l'état du thread Kafka
@app.get("/health/kafka")
async def kafka_health():
    threads = [
        {"name": t.name, "alive": t.is_alive(), "daemon": t.daemon}
        for t in threading.enumerate()
    ]
    return {
        "kafka_thread_exists": kafka_thread is not None,
        "kafka_thread_alive": kafka_thread.is_alive() if kafka_thread else False,
        "all_threads": threads
    }

logger.info("📝 Configuration de main.py terminée")