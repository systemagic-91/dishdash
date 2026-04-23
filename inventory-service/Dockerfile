# ---- Stage 1: Builder ----
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

COPY order-service/build.gradle.kts order-service/build.gradle.kts
COPY inventory-service/build.gradle.kts inventory-service/build.gradle.kts
COPY payment-service/build.gradle.kts payment-service/build.gradle.kts

RUN chmod +x gradlew && ./gradlew :inventory-service:dependencies --no-daemon

COPY inventory-service/src inventory-service/src

RUN ./gradlew :inventory-service:bootJar --no-daemon

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S dishdash && adduser -S dishdash -G dishdash
USER dishdash

COPY --from=builder /app/inventory-service/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]