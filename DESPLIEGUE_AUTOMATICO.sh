#!/bin/bash

# Script de Despliegue Automatizado para Recetas Spring
# Este script automatiza todo el proceso de despliegue

set -e  # Salir si hay algún error

echo "🚀 Iniciando despliegue automatizado de Recetas Spring..."
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ Error: No se encontró pom.xml. Ejecuta este script desde la raíz del proyecto.${NC}"
    exit 1
fi

# Paso 1: Compilar proyecto
echo -e "${YELLOW}📦 Paso 1: Compilando proyecto...${NC}"
mvn clean package -Dmaven.test.skip=true

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error en la compilación${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Compilación exitosa${NC}"
echo ""

# Paso 2: Verificar que el JAR existe
JAR_FILE="target/recetas-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}❌ Error: No se encontró el JAR en $JAR_FILE${NC}"
    exit 1
fi

echo -e "${GREEN}✅ JAR creado: $JAR_FILE${NC}"
echo ""

# Paso 3: Crear directorios necesarios
echo -e "${YELLOW}📁 Paso 2: Creando directorios necesarios...${NC}"
mkdir -p uploads/imagenes
mkdir -p uploads/videos
echo -e "${GREEN}✅ Directorios creados${NC}"
echo ""

# Paso 4: Verificar configuración
echo -e "${YELLOW}⚙️  Paso 3: Verificando configuración...${NC}"
if [ ! -f "src/main/resources/application.properties" ]; then
    echo -e "${RED}❌ Error: No se encontró application.properties${NC}"
    exit 1
fi

# Verificar variables de entorno o usar valores por defecto
DB_URL=${DB_URL:-"jdbc:mysql://localhost:3306/recetas"}
DB_USERNAME=${DB_USERNAME:-"root"}
DB_PASSWORD=${DB_PASSWORD:-"root"}

echo -e "${GREEN}✅ Configuración verificada${NC}"
echo "   DB_URL: ${DB_URL}"
echo "   DB_USERNAME: ${DB_USERNAME}"
echo ""

# Paso 5: Información de despliegue
echo -e "${YELLOW}📋 Paso 4: Información de despliegue${NC}"
echo ""
echo -e "${GREEN}✅ Proyecto listo para desplegar${NC}"
echo ""
echo "Para ejecutar la aplicación:"
echo "  java -jar $JAR_FILE"
echo ""
echo "O con variables de entorno:"
echo "  DB_URL=$DB_URL DB_USERNAME=$DB_USERNAME DB_PASSWORD=$DB_PASSWORD java -jar $JAR_FILE"
echo ""
echo "Para ejecutar en background:"
echo "  nohup java -jar $JAR_FILE > app.log 2>&1 &"
echo ""
echo -e "${GREEN}🎉 Despliegue preparado exitosamente!${NC}"

