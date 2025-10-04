# Build mərhələsi
FROM eclipse-temurin:21-jdk-alpine as build

WORKDIR /app

# Gradle fayllarını kopyala
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Asılılıqları endir (offline cache üçün)
RUN ./gradlew dependencies --no-daemon || return 0

# Layihə kodunu kopyala
COPY src ./src

# JAR faylını build et
RUN ./gradlew bootJar --no-daemon

# Runtime mərhələsi
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Build-dən çıxan jar-ı kopyala
COPY --from=build /app/build/libs/*.jar app.jar

# Proqramı işə sal
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
