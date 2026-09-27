FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY settings.xml ./
COPY pom.xml ./
RUN mvn -B -s settings.xml dependency:go-offline

COPY src ./src
RUN mvn -B -s settings.xml package -DskipTests

FROM eclipse-temurin:17-jre
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone
WORKDIR /app
COPY --from=build /app/target/elderly-assessment-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]