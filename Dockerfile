# Laufzeit-Image für das Krautkontroll-Backend.
# Erwartet ein bereits gebautes JAR unter target/ (wird in der CD-Pipeline gebaut).
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
