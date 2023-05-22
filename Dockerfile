FROM maven:3.8.1-adoptopenjdk-11 AS maven

WORKDIR /app
COPY . /app

RUN mvn package


FROM eclipse-temurin:11
EXPOSE 80

WORKDIR /opt/app

VOLUME /log
COPY --from=maven /app/src/main/resources/artifacts /opt/app/artifacts
COPY --from=maven /app/target/ecr-now.jar /opt/app/

ENTRYPOINT ["java","-Dspring.profiles.active=local","-jar","ecr-now.jar"]