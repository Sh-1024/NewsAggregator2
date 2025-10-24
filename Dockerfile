FROM  openjdk:25 AS build

WORKDIR /newsaggregator

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw package -DskipTests

FROM openjdk:25

WORKDIR /newsaggregator

COPY --from=build /newsaggregator/target/*.jar NewsAggregator.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "NewsAggregator.jar"]