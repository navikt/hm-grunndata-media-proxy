FROM ghcr.io/navikt/baseimages/temurin:17
USER root
RUN apt-get update
RUN apt-get install -y libjemalloc-dev
USER apprunner
COPY build/libs/hm-grunndata-media-proxy-all.jar ./app.jar
