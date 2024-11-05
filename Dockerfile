FROM azul/zulu-openjdk:17 as builder

WORKDIR /app

COPY . .

RUN ./mvnw package -DskipTests

FROM azul/zulu-openjdk:17 as runtime

WORKDIR /app

COPY --from=builder /app/target/*.jar /app/timetable-microservice.jar

EXPOSE 8083

CMD ["java", "-jar", "/app/timetable-microservice.jar"]
