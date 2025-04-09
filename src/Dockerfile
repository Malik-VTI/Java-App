FROM ubuntu:22.04

WORKDIR /app

# Copy binary dari hasil orchestrion build di Jenkins
COPY target/retail-service-1.0.jar .

# Expose port kalau perlu
EXPOSE 7070

CMD ["./retail-service-1.0.jar"]
