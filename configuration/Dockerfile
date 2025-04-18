FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml ./pom.xml
COPY configuration/pom.xml ./configuration/pom.xml

COPY configuration/src ./configuration/src
RUN mvn -f configuration/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/configuration/target/configuration-*.jar app.jar
EXPOSE 8888

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]