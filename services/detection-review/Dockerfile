FROM ebike-helmet-guard/detection-reivew-base:3.0

WORKDIR /app

COPY target/detection-review-0.0.1-SNAPSHOT.jar /app/detection-review.jar
COPY src/main/resources/model /app/model

EXPOSE 7000

ENV SPRING_CLOUD_NACOS_SERVER_ADDR=host.docker.internal:8848
ENV SPRING_PROFILES_ACTIVE=test

ENTRYPOINT ["java", "-jar", "/app/detection-review.jar"]