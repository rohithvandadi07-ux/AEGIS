FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -pl gateway-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/gateway-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
