FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -pl policy-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/policy-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
