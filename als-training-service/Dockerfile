FROM apache/spark:3.5.1

USER root

# Install Python, pip, cron
RUN apt-get update && \
    apt-get install -y python3 python3-pip cron && \
    pip3 install pandas numpy pyspark && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY train.py .
COPY crontab /etc/cron.d/als-cron

# Set permissions and install crontab
RUN chmod 0644 /etc/cron.d/als-cron && \
    crontab /etc/cron.d/als-cron

# Log file
RUN touch /var/log/cron.log

# Start cron and log output
CMD cron && tail -f /var/log/cron.log
