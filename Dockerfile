FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine-slim as runner

RUN mkdir -p /app && \
  addgroup -S appgroup -g 1000 && \
  adduser -S appuser -G appgroup -u 1000

COPY target/*-exec.jar /app/app.jar

USER appuser

WORKDIR /app