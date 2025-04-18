FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml ./pom.xml

COPY write-api/pom.xml ./write-api/pom.xml

COPY write-api/src ./write-api/src

RUN mvn -f write-api/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/write-api/target/write-api-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]