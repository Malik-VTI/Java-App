FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy binary dari hasil orchestrion build di Jenkins
COPY target/retail-service-1.0.jar app.jar

# Expose port kalau perlu
EXPOSE 7070

CMD ["java", "-jar", "app.jar"]
