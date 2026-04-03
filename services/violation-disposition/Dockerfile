FROM eclipse-temurin:17-jre

LABEL authors="czming"

COPY target/violation-disposition-0.0.1-SNAPSHOT.jar /app/violation-disposition.jar

EXPOSE 8000

ENV SPRING_PROFILES_ACTIVE=prod

ENV SPRING_CLOUD_NACOS_SERVER_ADDR=host.docker.internal:8848

ENTRYPOINT ["java", "-jar", "/app/violation-disposition.jar"]



