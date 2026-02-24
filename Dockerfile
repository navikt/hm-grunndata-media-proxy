FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache jemalloc-dev
ENV TZ="Europe/Oslo"
EXPOSE 8080
COPY build/libs/hm-grunndata-media-proxy-all.jar ./app.jar
USER 1000
CMD ["java", "-jar", "app.jar"]