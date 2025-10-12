# Use an official OpenJDK image
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy the project
COPY . .

# Build the application using Maven wrapper
RUN ./mvnw clean package -DskipTests

# Expose the port your Spring Boot app uses
EXPOSE 8080

# Run the app (Render provides the PORT env var automatically)
CMD ["sh", "-c", "java -jar target/*.jar"]
