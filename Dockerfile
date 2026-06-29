# ============================================================================
# DOCKERFILE - Product System (Spring Boot)
# Proyecto Final - Ingeniería de Software II
# ============================================================================
# Etapa 1: Construcción (Build)
# ============================================================================
FROM openjdk:8-jdk-alpine AS builder

WORKDIR /app

# Copiar archivos de Maven primero para cachear dependencias
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

# Descargar dependencias (esta capa se cachea si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src src

# Compilar y empaquetar (sin tests para build más rápido)
RUN ./mvnw clean package -DskipTests

# ============================================================================
# Etapa 2: Imagen final (Runtime)
# ============================================================================
FROM openjdk:8-jre-alpine

# Metadatos de la imagen
LABEL maintainer="Equipo IS2 - UCSP"
LABEL description="Product System - Aplicación web de gestión de productos"
LABEL version="1.0.0"
LABEL project="Proyecto Final Ingeniería de Software II"

# Crear usuario no-root para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=builder /app/target/spring-product-system-1.0-SNAPSHOT.jar app.jar

# Cambiar a usuario no-root
USER appuser

# Puerto expuesto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/ || exit 1

# Punto de entrada
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=prod", \
    "-jar", \
    "/app/app.jar"]
