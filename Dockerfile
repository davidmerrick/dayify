FROM gradle:6.7.1-jdk11 AS builder
COPY --chown=gradle:gradle . /home/application
WORKDIR /home/application

RUN gradle test --no-daemon
RUN gradle shadowJar --no-daemon

FROM openjdk:11.0.9-slim
COPY --from=builder /home/application/build/libs/application.jar \
    /home/application/

WORKDIR /home/application

CMD ["java", "-jar", "application.jar"]
