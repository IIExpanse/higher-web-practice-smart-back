FROM eclipse-temurin:11-jre
WORKDIR /app
COPY /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
