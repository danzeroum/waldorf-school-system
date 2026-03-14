# --- Stage 1: build ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# cache-bust: 2026-03-14
RUN ./mvnw -B package -DskipTests --no-transfer-progress 2>/dev/null || \
    (apk add --no-cache maven && mvn -B package -DskipTests --no-transfer-progress)

# --- Stage 2: runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S waldorf && adduser -S waldorf -G waldorf
USER waldorf

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","app.jar"]
