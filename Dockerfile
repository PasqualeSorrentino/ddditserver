# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copio solo i file necessari a risolvere le dipendenze
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY pom.xml .

# Permessi ed offline deps
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

# Ora copio il sorgente e compilo
COPY src ./src
RUN ./mvnw -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copio il fat-jar generato da Spring Boot
COPY --from=build /app/target/*.jar /app/app.jar

# Imposto una cartella di default per i modelli (puoi montarla come volume)
ENV MODELS_FOLDER_PATH=/data/models
VOLUME ["/data/models"]

# Spring Boot espone 8080
EXPOSE 8080

# JAVA_OPTS per Xms/Xmx, profili, ecc.
ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]
