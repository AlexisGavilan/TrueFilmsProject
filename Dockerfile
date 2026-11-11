# Etapa 1: Compilación usando Java 25 oficial y Maven instalado en Alpine
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app

# Instalar Maven en la imagen basada en Alpine con Java 25
RUN apk add --no-cache maven

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ligera para ejecución
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/TrueFilmsProject-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]