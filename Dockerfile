FROM gradle:7.6.1-jdk11 AS gradle
COPY --chown=gradle:gradle . /home/application
WORKDIR /home/application
RUN gradle buildNativeLayersTask
RUN gradle generateResourcesConfigFile
RUN gradle dockerPrepareContext

FROM ghcr.io/graalvm/native-image:ol7-java17-22.2.0 AS graalvm
WORKDIR /home/app
COPY --from=gradle /home/application/build/docker/native-main/layers/libs /home/app/libs
COPY --from=gradle /home/application/build/docker/native-main/layers/classes /home/app/classes
COPY --from=gradle /home/application/build/docker/native-main/layers/resources /home/app/resources
COPY --from=gradle /home/application/build/docker/native-main/layers/application.jar /home/app/application.jar
RUN mkdir /home/app/config-dirs
COPY --from=gradle /home/application/build/docker/native-main/config-dirs/generateResourcesConfigFile /home/app/config-dirs/generateResourcesConfigFile
RUN native-image -cp /home/app/libs/*.jar:/home/app/resources:/home/app/application.jar --initialize-at-build-time=biweekly.Biweekly --no-fallback -H:Name=application -J--add-exports=org.graalvm.nativeimage.builder/com.oracle.svm.core.jdk=ALL-UNNAMED -J--add-exports=org.graalvm.nativeimage.builder/com.oracle.svm.core.configure=ALL-UNNAMED -J--add-exports=org.graalvm.sdk/org.graalvm.nativeimage.impl=ALL-UNNAMED -H:ConfigurationFileDirectories=/home/app/config-dirs/generateResourcesConfigFile -H:Class=io.github.davidmerrick.dayify.Application

FROM frolvlad/alpine-glibc:alpine-3.12
RUN apk --no-cache update && apk add libstdc++
EXPOSE 8080
COPY --from=graalvm /home/app/application /app/application
ENTRYPOINT ["/app/application", "-Duser.timezone=America/Los_Angeles"]
