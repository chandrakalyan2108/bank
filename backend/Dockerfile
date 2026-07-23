# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN addgroup --system spring && adduser --system --ingroup spring spring
COPY --from=build /app/target/banking-app.jar app.jar
RUN chown spring:spring app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
