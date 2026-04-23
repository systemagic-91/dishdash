FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

COPY order-service/build.gradle.kts order-service/build.gradle.kts
COPY inventory-service/build.gradle.kts inventory-service/build.gradle.kts
COPY payment-service/build.gradle.kts payment-service/build.gradle.kts

RUN chmod +x gradlew && ./gradlew :payment-service:dependencies --no-daemon

COPY payment-service/src payment-service/src

RUN ./gradlew :payment-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S dishdash && adduser -S dishdash -G dishdash
USER dishdash

COPY --from=builder /app/payment-service/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]