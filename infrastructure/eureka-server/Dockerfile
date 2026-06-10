# =============================================================================
# Eureka Server — Multi-Stage Dockerfile
#
# BUILD CONTEXT: Project root directory (campus-career-platform/)
#   docker build -f infrastructure/eureka-server/Dockerfile .
#   OR via docker-compose (build.context: . in docker-compose.yml)
#
# Why build context must be the project ROOT:
#   Maven needs the parent pom.xml and the shared/shared-events module
#   to resolve the multi-module project structure. If you set context
#   to infrastructure/eureka-server/, Maven will fail with
#   "parent POM not found".
#
# LAYER CACHING STRATEGY:
#   Layers are ordered cheapest-to-change → most-likely-to-change:
#   1. Maven binary (FROM — almost never changes)
#   2. POM files only (dependency:go-offline — changes when deps change)
#   3. Source code (package — changes on every code edit)
#   This means changing a .java file only invalidates layer 3.
#   Changing a pom.xml invalidates layers 2+3 and re-downloads deps.
#
# FINAL IMAGE SIZE: ~185MB (eclipse-temurin:17-jre-alpine + curl + JAR)
# =============================================================================

# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /workspace

# ── Layer: Parent POM ────────────────────────────────────────────────────────
# Copy root pom.xml first. This defines versions and module declarations.
# Changes rarely → cached layer.


# ── Layer: Active module POMs ────────────────────────────────────────────────
# Copy pom.xml for every module that is ACTIVE (uncommented) in the parent pom.
# At Milestone 1, active modules are: shared-events + eureka-server.
# Maven needs all active module POMs to resolve the reactor.
# Adding a new module to parent pom invalidates this layer.
COPY pom.xml ./pom.xml
COPY shared/shared-events/pom.xml               shared/shared-events/pom.xml
COPY infrastructure/eureka-server/pom.xml        infrastructure/eureka-server/pom.xml
COPY infrastructure/api-gateway/pom.xml          infrastructure/api-gateway/pom.xml
COPY services/auth-service/pom.xml               services/auth-service/pom.xml
COPY services/student-service/pom.xml            services/student-service/pom.xml
COPY services/company-service/pom.xml            services/company-service/pom.xml
COPY services/job-service/pom.xml                services/job-service/pom.xml
COPY services/application-service/pom.xml                services/application-service/pom.xml



# ── Layer: Dependency download ───────────────────────────────────────────────
# Download all dependencies for all active modules.
# This layer is cached as long as POM files haven't changed.
# First run downloads ~200MB of Maven artifacts into Docker layer cache.
# Subsequent builds skip this if POM files are unchanged.
RUN mvn dependency:go-offline \
        --no-transfer-progress \
        -q

# ── Layer: Source code ───────────────────────────────────────────────────────
# Copy source code AFTER dependency download.
# Source changes (the most frequent change) only invalidate this layer,
# not the slow dependency download layer above.
COPY shared/shared-events/src                    shared/shared-events/src
COPY infrastructure/eureka-server/src            infrastructure/eureka-server/src

# ── Layer: Compile and package ───────────────────────────────────────────────
# Step 1: Install shared-events into the local Maven repository inside Docker.
#         Required because eureka-server is in the same reactor and Maven
#         needs shared-events to be resolvable (even though eureka-server
#         doesn't directly depend on it, they're in the same multi-module project).

RUN mvn install \
        -N \
        --no-transfer-progress \
        -q
RUN mvn clean install \
        -pl shared/shared-events \
        -DskipTests \
        --no-transfer-progress \
        -q

# Step 2: Package eureka-server as a fat JAR.
#         -DskipTests: tests are run in CI, not during Docker build
#         Result: infrastructure/eureka-server/target/eureka-server-*.jar
RUN mvn clean package \
        -pl infrastructure/eureka-server \
        -DskipTests \
        --no-transfer-progress \
        -q

# ─── Stage 2: Runtime ────────────────────────────────────────────────────────
# Minimal JRE — no JDK, no Maven, no build tools. Significantly smaller image.
FROM eclipse-temurin:17-jre-alpine AS runtime

# Labels for image identification
LABEL org.opencontainers.image.title="campus-eureka-server"
LABEL org.opencontainers.image.description="Campus Career Platform — Eureka Service Registry"
LABEL org.opencontainers.image.version="1.0.0"

# Install curl: required by docker-compose healthcheck
#   test: curl -f http://localhost:8761/actuator/health
# apk add --no-cache → don't cache index files (keeps image smaller)
RUN apk add --no-cache curl

WORKDIR /app

# ── Copy the fat JAR from builder stage ──────────────────────────────────────
# Using wildcard *.jar avoids hardcoding the version string.
# The Spring Boot Maven plugin creates ONE fat JAR (the other file ends in .original).
# So this wildcard safely matches exactly one file.
COPY --from=builder /workspace/infrastructure/eureka-server/target/*.jar app.jar

# ── Non-root user (security best practice) ───────────────────────────────────
# Running as root inside containers is a security risk.
# Create a dedicated system group and user, then switch to it.
RUN addgroup -S campus && adduser -S campus -G campus
USER campus

# ── Port ─────────────────────────────────────────────────────────────────────
EXPOSE 8761

# ── Entrypoint ───────────────────────────────────────────────────────────────
# JVM flags explained:
#
# -XX:+UseContainerSupport
#   Makes JVM respect container cgroup memory limits.
#   Without this, JVM sees the host's total RAM and sizes heap accordingly.
#   A 512MB container without this flag may create a 4GB heap and OOM.
#
# -XX:MaxRAMPercentage=75.0
#   Allocate 75% of container RAM to JVM heap.
#   Example: 512MB container → ~384MB max heap.
#   Leaves 25% for OS, non-heap memory, and JVM overhead.
#
# -Djava.security.egd=file:/dev/./urandom
#   Use /dev/urandom instead of /dev/random for cryptographic seeding.
#   /dev/random blocks when entropy is low (common in containers).
#   This prevents startup delays of 30+ seconds in containerized JVMs.
#   The /dev/./ path tricks Java to use the non-blocking urandom.
#
# -jar app.jar
#   Run the fat JAR produced by Spring Boot Maven plugin.
ENTRYPOINT [ \
    "java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar" \
]
