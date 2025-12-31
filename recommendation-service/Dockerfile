<<<<<<< HEAD
FROM apache/spark:3.5.1

USER root

RUN apt-get update && \
    apt-get install -y python3-pip && \
    pip3 install fastapi uvicorn pyspark numpy && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY app.py .

CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8080", "--workers", "1"]
=======
FROM python:3.11-slim

# Installer Java pour Spark
RUN apt-get update && \
    apt-get install -y default-jdk curl && \
    rm -rf /var/lib/apt/lists/*

RUN python3 -m pip install --upgrade pip

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app/ ./app

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
>>>>>>> d1316330500bc9325d5d1c7fdae61ff4162b7b47
