FROM openjdk:17-alpine
WORKDIR /app
COPY ./build/libs/socks-drawer-1.0-SNAPSHOT.jar /app/app.jar
CMD [ "java", "-jar", "/app/app.jar"]