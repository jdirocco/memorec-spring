FROM maven:3.8.7-eclipse-temurin-19-alpine

WORKDIR /app

COPY . .
EXPOSE 8080
CMD mvn --version
CMD mvn spring-boot:run -X