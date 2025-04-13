
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY . .

RUN mvn clean package -pl :read-api -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app


COPY --from=builder /workspace/read-api/target/read-api-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]