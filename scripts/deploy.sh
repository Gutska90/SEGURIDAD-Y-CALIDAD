#!/bin/bash

# Script de despliegue para máquina virtual
# Uso: ./scripts/deploy.sh

set -e

echo "=========================================="
echo "Despliegue de Recetas Spring"
echo "=========================================="

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}Error: No se encontró pom.xml. Ejecuta este script desde la raíz del proyecto.${NC}"
    exit 1
fi

# Verificar Java
echo -e "${YELLOW}Verificando Java...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: Java no está instalado. Por favor instala Java 17.${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo -e "${RED}Error: Se requiere Java 17 o superior. Versión actual: $JAVA_VERSION${NC}"
    exit 1
fi
echo -e "${GREEN}Java $JAVA_VERSION encontrado${NC}"

# Verificar Maven
echo -e "${YELLOW}Verificando Maven...${NC}"
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Error: Maven no está instalado.${NC}"
    exit 1
fi
echo -e "${GREEN}Maven encontrado${NC}"

# Compilar el proyecto
echo -e "${YELLOW}Compilando proyecto...${NC}"
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}Error: La compilación falló.${NC}"
    exit 1
fi
echo -e "${GREEN}Compilación exitosa${NC}"

# Crear directorio de uploads si no existe
echo -e "${YELLOW}Creando directorio de uploads...${NC}"
mkdir -p uploads
chmod 755 uploads

# Verificar que el JAR se creó
JAR_FILE=$(find target -name "recetas-*.jar" -not -name "*-sources.jar" | head -n 1)
if [ -z "$JAR_FILE" ]; then
    echo -e "${RED}Error: No se encontró el archivo JAR compilado.${NC}"
    exit 1
fi

echo -e "${GREEN}JAR encontrado: $JAR_FILE${NC}"

# Crear script de inicio
echo -e "${YELLOW}Creando script de inicio...${NC}"
cat > start-app.sh << 'EOF'
#!/bin/bash
cd "$(dirname "$0")"
JAR_FILE=$(find target -name "recetas-*.jar" -not -name "*-sources.jar" | head -n 1)
java -jar "$JAR_FILE"
EOF

chmod +x start-app.sh

echo -e "${GREEN}=========================================="
echo -e "Despliegue completado exitosamente"
echo -e "==========================================${NC}"
echo ""
echo "Para iniciar la aplicación, ejecuta:"
echo "  ./start-app.sh"
echo ""
echo "O directamente:"
echo "  java -jar $JAR_FILE"
echo ""
echo "La aplicación estará disponible en:"
echo "  http://localhost:8080/recetas"
echo ""

