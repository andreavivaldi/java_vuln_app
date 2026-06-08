# Deliberately old base image with known CVEs for demo purposes
FROM eclipse-temurin:11-jre-jammy

LABEL maintainer="demo@sysdig.com"
LABEL description="Intentionally vulnerable Spring Boot app for Sysdig pipeline scanning demo"

WORKDIR /app

COPY target/vulnapp-0.0.1-SNAPSHOT.jar app.jar

# Running as root intentionally (bad practice for demo)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
