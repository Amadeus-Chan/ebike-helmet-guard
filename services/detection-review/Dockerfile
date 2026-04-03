FROM nvidia/cuda:12.8.0-cudnn-runtime-ubuntu22.04

WORKDIR /app

ENV DEBIAN_FRONTEND=noninteractive

ENV MODEL_DETECTION_MODE=CPU

RUN sed -i 's@http://archive.ubuntu.com/ubuntu/@https://mirrors.aliyun.com/ubuntu/@g' /etc/apt/sources.list && \
    sed -i 's@http://security.ubuntu.com/ubuntu/@https://mirrors.aliyun.com/ubuntu/@g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get -o Acquire::Retries=5 install -y --no-install-recommends \
        openjdk-17-jre-headless \
        rdma-core \
        librdmacm1 \
        libibverbs1 \
        ibverbs-providers \
        libmlx5-1 \
        libnuma1 \
        libnl-3-200 && \
    rm -rf /var/lib/apt/lists/*

COPY target/detection-review-0.0.1-SNAPSHOT.jar /app/detection-review.jar
COPY target/classes/model /app/model

EXPOSE 7000

ENV SPRING_CLOUD_NACOS_SERVER_ADDR=host.docker.internal:8848
ENV SPRING_PROFILES_ACTIVE=prod
ENV DJL_CACHE_DIR=/root/.djl.ai

ENTRYPOINT ["java", "-jar", "/app/detection-review.jar"]