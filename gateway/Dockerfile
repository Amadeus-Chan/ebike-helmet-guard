FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/gateway-0.0.1-SNAPSHOT.jar gateway.jar

EXPOSE 80

ENV SPRING_PROFILES_ACTIVE=prod

ENV SPRING_CLOUD_NACOS_SERVER_ADDR=host.docker.internal:8848

ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]