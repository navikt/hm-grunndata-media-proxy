FROM ghcr.io/navikt/java:20
USER root
RUN apt-get update && apt-get install -y curl
USER apprunner
COPY build/libs/hm-grunndata-media-proxy-all.jar ./app.jar
