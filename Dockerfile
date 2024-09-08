#FROM sbtscala/scala-sbt:eclipse-temurin-alpine-21.0.2_13_1.10.1_3.5.0
#
## Set the working directory
#WORKDIR /app
#
## Copy the sbt executable
#COPY --from=hseeberger/scala-sbt:graalvm-ce-21.3.0-java17_1.6.2_3.1.1 /root/.sdkman/candidates/sbt/current/bin/sbt /usr/local/bin/sbt
#
## Copy the project files to the container
#COPY . .
#
## Run sbt to compile the project and create the assembly JAR
#RUN sbt clean compile assembly
#
## Define the command to run your application
##CMD ["java", "-jar", "target/scala-2.13/your-scala-cats-project-assembly-0.1.jar"]
#
#WORKDIR /app
#
##COPY --from=hseeberger/scala-sbt:graalvm-ce-21.3.0-java17_1.6.2_3.1.1 /root/.sdkman/candidates/sbt/current/bin/sbt /usr/local/bin/sbt
#
#CMD ["java", "-jar", "target/scala-3.4.2/green-screen-assembly-0.1.0-SNAPSHOT.jar"]
