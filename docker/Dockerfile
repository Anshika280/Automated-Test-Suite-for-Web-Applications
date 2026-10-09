# Production Dockerfile for Automated Test Suite (ATS)
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and pre-fetch dependencies for efficient Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code, test resources, and entrypoint script
COPY src ./src
COPY docker-entrypoint.sh .

# Pre-compile framework and test classes
RUN mvn test-compile -B

# Execution Stage with Eclipse Temurin JDK 17
FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

# Copy built application and cached dependencies from builder
COPY --from=builder /app /app
COPY --from=builder /root/.m2 /root/.m2

RUN chmod +x /app/docker-entrypoint.sh

# Entrypoint script waits for Grid readiness before running Maven test execution
ENTRYPOINT ["/app/docker-entrypoint.sh"]
CMD ["mvn", "test", "-Dheadless=true"]
