# app/kafka_listener.py
import logging
import sys
import time
from kafka import KafkaConsumer, KafkaProducer
from kafka.errors import NoBrokersAvailable, KafkaError

sys.stdout.reconfigure(line_buffering=True)
logger = logging.getLogger(__name__)

def start_kafka_listener():
    logger.info("=" * 60)
    logger.info("🎯 ENTRÉE DANS start_kafka_listener()")
    logger.info("=" * 60)
    
    max_retries = 10
    retry_delay = 5
    
    consumer = None
    producer = None
    
    # Connexion à Kafka
    for attempt in range(max_retries):
        try:
            logger.info(f"🔄 Tentative {attempt + 1}/{max_retries} de connexion à Kafka...")
            
            consumer = KafkaConsumer(
                'preprocessing-trigger',
                bootstrap_servers='kafka:9092',
                auto_offset_reset='earliest',
                group_id='preprocessing-group',
                value_deserializer=lambda m: m.decode('utf-8'),
                enable_auto_commit=True,
                consumer_timeout_ms=1000  # Timeout pour ne pas bloquer indéfiniment
            )
            
            logger.info("✅ Consumer Kafka créé avec succès")
            
            producer = KafkaProducer(
                bootstrap_servers='kafka:9092',
                value_serializer=lambda v: v.encode('utf-8')
            )
            
            logger.info("✅ Producer Kafka créé avec succès")
            break
            
        except NoBrokersAvailable as e:
            logger.warning(f"⏳ Kafka non disponible: {e}")
            if attempt < max_retries - 1:
                logger.info(f"   Nouvelle tentative dans {retry_delay}s...")
                time.sleep(retry_delay)
            else:
                logger.error("❌ Impossible de se connecter à Kafka")
                return
                
        except Exception as e:
            logger.error(f"❌ Erreur inattendue: {e}")
            import traceback
            traceback.print_exc()
            return
    
    if consumer is None or producer is None:
        logger.error("❌ Consumer ou Producer non initialisé")
        return
    
    # Import du service
    try:
        logger.info("📦 Import de PreprocessingService...")
        from app.services.preprocessing_service import PreprocessingService
        preprocessor = PreprocessingService()
        logger.info("✅ PreprocessingService initialisé")
    except Exception as e:
        logger.error(f"❌ Erreur import PreprocessingService: {e}")
        import traceback
        traceback.print_exc()
        return
    
    logger.info("=" * 80)
    logger.info("👂 KAFKA LISTENER EN ÉCOUTE SUR 'preprocessing-trigger'")
    logger.info("=" * 80)
    sys.stdout.flush()
    
    # Boucle d'écoute avec gestion du timeout
    message_count = 0
    
    while True:
        try:
            # Poll avec timeout pour éviter le blocage
            message_batch = consumer.poll(timeout_ms=1000, max_records=10)
            
            if not message_batch:
                # Aucun message, on continue la boucle
                # Log périodique pour montrer que le listener est actif
                if message_count == 0:
                    logger.info("⏳ En attente de messages...")
                    message_count = 1  # Pour éviter de spammer les logs
                continue
            
            # Traiter les messages reçus
            for topic_partition, messages in message_batch.items():
                for message in messages:
                    logger.info("=" * 80)
                    logger.info(f"📨 MESSAGE REÇU!")
                    logger.info(f"   Topic: {message.topic}")
                    logger.info(f"   Partition: {message.partition}")
                    logger.info(f"   Offset: {message.offset}")
                    logger.info(f"   Value: {message.value}")
                    logger.info("=" * 80)
                    sys.stdout.flush()
                    
                    event = message.value
                    
                    try:
                        logger.info("🔄 Démarrage du preprocessing...")
                        
                        csv_path = preprocessor.run()
                        
                        logger.info(f"✅ Preprocessing terminé: {csv_path}")
                        
                        # Envoyer l'événement de fin
                        producer.send('training-trigger', 'preprocessing-done')
                        producer.flush()
                        
                        logger.info("📤 Événement 'preprocessing-done' envoyé")
                        
                    except Exception as e:
                        logger.error(f"❌ Erreur traitement message: {e}")
                        import traceback
                        traceback.print_exc()
                        
        except Exception as e:
            logger.error(f"❌ Erreur dans la boucle d'écoute: {e}")
            import traceback
            traceback.print_exc()
            time.sleep(5)  # Attendre avant de réessayer