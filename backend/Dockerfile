FROM alpine:3.21 AS build
RUN apk add --no-cache openjdk17-jdk maven
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -B -q

FROM alpine:3.21
RUN apk add --no-cache openjdk17-jre-headless
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
