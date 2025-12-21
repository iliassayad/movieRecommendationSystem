FROM apache/spark:3.5.1

USER root

RUN apt-get update && \
    apt-get install -y python3-pip && \
    pip3 install fastapi uvicorn pyspark numpy && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY app.py .

CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8080", "--workers", "1"]
