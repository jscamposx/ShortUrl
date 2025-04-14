
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY . .

RUN mvn clean package -pl :read-api -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app


RUN apk add --no-cache netcat-openbsd
RUN apk add --no-cache curl

COPY --from=builder /workspace/read-api/target/read-api-*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["sh", "-c", "echo '>>> Waiting 21s before starting Java application...' && sleep 21 && java -jar /app/app.jar"]
