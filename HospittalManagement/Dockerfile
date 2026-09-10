# Step 1: Maven se pehle project ko container ke andar build karo
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


# Step 2: Final lightweight image banao aur JAR copy karo
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar


# Step 3: Run command
ENTRYPOINT ["java", "-jar", "app.jar"]






## Step 1: Java environment base image
#FROM eclipse-temurin:21-jdk-alpine
#
## Step 2: Working directory set karo container ke andar
#WORKDIR /app
#
## Step 3: Apne target folder se jar file ko container me copy karo
#COPY target/*.jar app.jar
#
## Step 4: Spring boot run karne ki command
#ENTRYPOINT ["java", "-jar", "app.jar"]