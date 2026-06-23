# Dockerfile for Inventory Service
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="WMS Team <wms-team@maersk.com>"
LABEL service="inventory-service"
LABEL version="1.0.0"

# Create non-root user
RUN addgroup -g 1001 wms && adduser -u 1001 -G wms -s /bin/sh -D wms

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy JAR file
ARG JAR_FILE=inventory-modernization/inventory-app/target/*.jar
COPY ${JAR_FILE} app.jar

# Change ownership
RUN chown -R wms:wms /app

USER wms

# Service configuration
ENV SERVER_PORT=8081
ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_APPLICATION_NAME=inventory-service

# JVM configuration
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
