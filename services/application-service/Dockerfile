FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY shared/shared-events/pom.xml               shared/shared-events/pom.xml
COPY infrastructure/eureka-server/pom.xml        infrastructure/eureka-server/pom.xml
COPY infrastructure/api-gateway/pom.xml          infrastructure/api-gateway/pom.xml
COPY services/auth-service/pom.xml               services/auth-service/pom.xml
COPY services/student-service/pom.xml            services/student-service/pom.xml
COPY services/company-service/pom.xml            services/company-service/pom.xml
COPY services/job-service/pom.xml                services/job-service/pom.xml
COPY services/application-service/pom.xml                services/application-service/pom.xml

RUN mvn dependency:go-offline \
        --no-transfer-progress \
        -q

COPY shared/shared-events/src                    shared/shared-events/src
COPY services/application-service/src                    services/application-service/src

RUN mvn install \
        -N \
        --no-transfer-progress \
        -q

RUN mvn clean install \
        -pl shared/shared-events \
        -DskipTests \
        --no-transfer-progress \
        -q

RUN mvn clean package \
        -pl services/job-service \
        -DskipTests \
        --no-transfer-progress \
        -q

FROM eclipse-temurin:17-jre-alpine AS runtime

LABEL org.opencontainers.image.title="campus-application-service"

RUN apk add --no-cache curl

WORKDIR /app

COPY --from=builder /workspace/services/application-service/target/*.jar app.jar

RUN addgroup -S campus && adduser -S campus -G campus
USER campus

EXPOSE 8085

ENTRYPOINT [ \
    "java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar" \
]
