# Dockerfile para la aplicación Spring Boot
FROM openjdk:17-jdk-slim

# Información del mantenedor
LABEL maintainer="Recetas Spring Team"

# Crear directorio de trabajo
WORKDIR /app

# Copiar el JAR de la aplicación
COPY target/recetas-*.jar app.jar

# Crear directorio para uploads
RUN mkdir -p /app/uploads

# Exponer el puerto 8080
EXPOSE 8080

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

