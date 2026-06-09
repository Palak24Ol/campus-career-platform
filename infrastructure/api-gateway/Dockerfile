
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /workspace


COPY pom.xml ./pom.xml


COPY shared/shared-events/pom.xml               shared/shared-events/pom.xml
COPY infrastructure/eureka-server/pom.xml        infrastructure/eureka-server/pom.xml
COPY infrastructure/api-gateway/pom.xml          infrastructure/api-gateway/pom.xml
COPY services/auth-service/pom.xml               services/auth-service/pom.xml
COPY services/student-service/pom.xml            services/student-service/pom.xml
COPY services/company-service/pom.xml            services/company-service/pom.xml
RUN mvn dependency:go-offline \
        --no-transfer-progress \
        -q

COPY shared/shared-events/src                    shared/shared-events/src
COPY infrastructure/api-gateway/src              infrastructure/api-gateway/src


RUN mvn clean install \
        -pl shared/shared-events \
        -DskipTests \
        --no-transfer-progress \
        -q


RUN mvn clean package \
        -pl infrastructure/api-gateway \
        -DskipTests \
        --no-transfer-progress \
        -q

FROM eclipse-temurin:17-jre-alpine AS runtime

LABEL org.opencontainers.image.title="campus-api-gateway"
LABEL org.opencontainers.image.description="Campus Career Platform — API Gateway"
LABEL org.opencontainers.image.version="1.0.0"

RUN apk add --no-cache curl

WORKDIR /app

COPY --from=builder /workspace/infrastructure/api-gateway/target/*.jar app.jar

RUN addgroup -S campus && adduser -S campus -G campus
USER campus

EXPOSE 8080

ENTRYPOINT [ \
    "java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar" \
]
