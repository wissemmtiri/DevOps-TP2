# Stage 1:
FROM maven:3.8.5-openjdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Stage 2:
FROM openjdk:11-jre-slim
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
WORKDIR /app
COPY --from=build /app/target/app.jar app.jar
EXPOSE 8081
USER appuser
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-jar", "app.jar"]