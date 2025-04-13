
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY . .

RUN mvn clean package -pl :write-api -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app


COPY --from=builder /workspace/write-api/target/write-api-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]