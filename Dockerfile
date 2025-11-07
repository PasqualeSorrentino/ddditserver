# Base image con Java 17
FROM eclipse-temurin:17-jdk-alpine

#Cartella di lavoro nel Container
WORKDIR /app

#Copia il jar nel container
COPY target/ddditserver.jar app.jar

#Espone la porta di Spring Boot
EXPOSE 8080

#Comando per avviare l'app
ENTRYPOINT ["java","-jar","app.jar"]

