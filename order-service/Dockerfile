# ---- STAGE 1: BUILDER ----
# Usamos a imagem completa do JDK para compilar
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Criamos os arquivo de build primeiro, separado do codigo fonte
# porque o docker tem cache  por camada. Se o codigo mudar,
# o gradle nao precisa baixar dependencias de novo (usa o cache)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copia os build.gradle.kts de TODOS os subprojetos
# O Gradle precisa que todos os diretórios do settings.gradle.kts existam
COPY order-service/build.gradle.kts order-service/build.gradle.kts
COPY inventory-service/build.gradle.kts inventory-service/build.gradle.kts
COPY payment-service/build.gradle.kts payment-service/build.gradle.kts

# Baixa dependencias (essa camada fica em cache se o build.gradle nao mudar)
RUN chmod +x gradlew && ./gradlew :order-service:dependencies --no-daemon

# Agora copia o codigo fonte e compila
COPY order-service/src order-service/src

RUN ./gradlew :order-service:bootJar --no-daemon

# ---- STAGE 3: RUNTIME ----
# Imagem minima necessária para rodar o .jar
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Usuário nao-root -> boa pratica de seguranca
# Um processo rodando como root dentro do container tem
# mais poder se houver uma brecha de segurança
RUN addgroup -S dishdash && adduser -S dishdash -G dishdash
USER dishdash

# Copia só o .jar do stage anterior
COPY --from=builder /app/order-service/build/libs/*.jar app.jar

# Documenta o que o container expoe a porta 8080
# (nao publica automaticamente, isso é feito no docker-compose)
EXPOSE 8080

# Flags importantes:
# -XX:+UseContainerSupport -> JVM respeita os limites de memória do container
# -XX:MaxRAMPercentage=75 -> usa no maximo 75% da RAM disponível no container
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]

