# Base image
FROM openjdk:17-jdk-alpine

# Install Korean locale and set timezone
RUN apk add --no-cache tzdata fontconfig ttf-dejavu ttf-nanum \
    && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone \
    && fc-cache -f -v \


# Set env for JVM to recognize the locale
ENV LANG=ko_KR.UTF-8 \
    LANGUAGE=ko.KR_UTF-8 \
    LC_ALL=ko.KR_UTF-8

# Set working directory
WORKDIR /app

# Copy application jar
COPY target/*.jar /app/app.jar

# Run application entrypoint
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]