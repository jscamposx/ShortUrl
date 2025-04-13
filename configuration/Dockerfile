
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY . .

RUN mvn clean package -pl :configuration -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app


COPY --from=builder /workspace/configuration/target/configuration-*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app/app.jar"]