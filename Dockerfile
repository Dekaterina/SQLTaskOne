FROM openjdk:17-jdk-slim

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    iputils-ping \
    curl \
    netcat-openbsd && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Копируем JAR файл (укажите правильное имя)
COPY app-deadline.jar app.jar

EXPOSE 9999

# ENTRYPOINT заменен на CMD для совместимости с docker-compose command
CMD ["java", "-jar", "app.jar"]