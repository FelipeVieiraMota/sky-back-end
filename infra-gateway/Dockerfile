FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only the pom first to leverage Docker layer caching for dependencies
COPY pom.xml .
# Pre-fetch dependencies (offline cache)
RUN mvn -q -DskipTests dependency:go-offline

# Now copy the sources and build
COPY src ./src
RUN mvn -q clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

# Optional: allow passing JVM flags at runtime: `-e JAVA_OPTS="-Xms256m -Xmx512m"`
#ENV JAVA_OPTS=""

# Copy the built jar from the builder image
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 80

# Use exec form; honors JAVA_OPTS and forwards signals properly
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]