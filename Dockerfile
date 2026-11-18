FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25 as runtime
FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25-dev as builder
USER root

COPY --from=runtime / /runtime/
RUN apk --root /runtime --initdb --repositories-file /etc/apk/repositories --no-cache add jemalloc

FROM runtime
COPY --from=builder /runtime/ /

WORKDIR /app
ENV LD_PRELOAD=/usr/lib/libjemalloc.so.2
ENV TZ=Europe/Oslo
EXPOSE 8080
COPY build/libs/hm-grunndata-media-proxy-all.jar ./app.jar
USER 1000
ENTRYPOINT ["java", "-jar", "app.jar"]