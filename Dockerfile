# fetched from https://stackoverflow.com/questions/27767264/how-to-dockerize-a-maven-project-how-many-ways-to-accomplish-it
# and slightly modified

#
# Build stage
#
FROM maven:eclipse-temurin AS build
ENV HOME=/usr/app
WORKDIR $HOME

# Copy the project files
COPY ciapp/ $HOME/

# Run the build with dependency cache
RUN --mount=type=cache,target=/root/.m2 mvn -f pom.xml clean package

#
# Package stage
#
FROM maven:eclipse-temurin
ARG JAR_FILE=/usr/app/target/ciapp-1.0-SNAPSHOT.jar

# Copy the built JAR from the build stage
COPY --from=build $JAR_FILE /app/runner.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/runner.jar"]
