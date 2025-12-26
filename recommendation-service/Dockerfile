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
