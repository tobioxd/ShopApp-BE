# build jar from source code using maven 
FROM azul/zulu-openjdk-alpine:21-latest AS build-stage
WORKDIR /build
# resolve dependencies 
COPY pom.xml mvnw /build/
COPY .mvn /build/.mvn
RUN --mount=type=cache,target=/root/.m2 ./mvnw verify clean --fail-never

# build jar
COPY . /build/
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package

FROM azul/zulu-openjdk-alpine:21-jre-headless-latest AS production-stage
WORKDIR /app
COPY --from=build-stage /build/target/*.jar app.jar
EXPOSE 80
CMD ["java", "-jar", "app.jar"]


