#FROM ubuntu:latest
LABEL authors="lgoldfinch"

#ENTRYPOINT ["top", "-b"]

FROM openjdk:21-jre-slim

WORKDIR /app

COPY --from=hseeberger/scala-sbt:11.0.11_1.5.5_2.13.6 /root/.sdkman/candidates/sbt/current/bin/sbt /usr/local/bin/sbt

CMD ["java", "-jar", "target/scala-2.13/your-scala-cats-project-assembly-0.1.jar"]
