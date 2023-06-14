FROM maven:3.8.7-eclipse-temurin-19-alpine

WORKDIR /app

COPY . .
EXPOSE 8080
RUN mvn clean install
CMD mvn spring-boot:run -X