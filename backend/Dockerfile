# --- Etapa 1: compilar el JAR ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Cachea las dependencias: solo se re-descargan si cambia el pom.xml.
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests clean package

# --- Etapa 2: imagen de ejecución (solo JRE, más liviana) ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Ajusta el heap de la JVM al tamaño del contenedor (útil en máquinas de 2 GB).
ENV JAVA_OPTS="-XX:MaxRAMPercentage=60"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
