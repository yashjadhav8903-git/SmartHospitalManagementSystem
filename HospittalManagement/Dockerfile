# Step 1: Java environment base image
FROM eclipse-temurin:21-jdk-alpine

# Step 2: Working directory set karo container ke andar
WORKDIR /app

# Step 3: Apne target folder se jar file ko container me copy karo
COPY target/*.jar app.jar

# Step 4: Spring boot run karne ki command
ENTRYPOINT ["java", "-jar", "app.jar"]