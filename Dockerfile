# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Package application (skip test execution during Docker image build)
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Run as a non-privileged container user
RUN useradd -m intellistock && chown -R intellistock:intellistock /app
USER intellistock

COPY --from=build /app/target/*.jar app.jar

# Render injects PORT dynamically (defaults to 8080 or 10000)
ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -Dspring.datasource.url=\"${SPRING_DATASOURCE_URL}\" -Dspring.datasource.username=\"${SPRING_DATASOURCE_USERNAME}\" -Dspring.datasource.password=\"${SPRING_DATASOURCE_PASSWORD}\" -jar app.jar"]
