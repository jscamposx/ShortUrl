# --- Stage 1: Build ---
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY . .

RUN mvn clean package -pl :gateway-api -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=builder /workspace/gateway-api/target/gateway-api-*.jar app.jar

EXPOSE 8083


ENTRYPOINT ["sh", "-c", "java -jar app.jar"]