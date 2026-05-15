FROM alpine:3.21 AS build
RUN apk add --no-cache openjdk17-jdk maven
ENV MAVEN_OPTS="-Xmx384m"
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B -q 2>/dev/null || true
COPY src ./src
RUN mvn package -DskipTests -B -q

FROM alpine:3.21
RUN apk add --no-cache openjdk17-jre-headless
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]