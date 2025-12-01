#!/bin/bash

# Script de configuración inicial para máquina virtual Ubuntu/Debian
# Uso: sudo ./scripts/setup-vm.sh

set -e

echo "=========================================="
echo "Configuración de Máquina Virtual"
echo "Recetas Spring - Semana 6"
echo "=========================================="

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Verificar que se ejecuta como root
if [ "$EUID" -ne 0 ]; then 
    echo -e "${RED}Por favor ejecuta este script como root (sudo)${NC}"
    exit 1
fi

# Actualizar sistema
echo -e "${YELLOW}Actualizando sistema...${NC}"
apt-get update
apt-get upgrade -y

# Instalar Java 17
echo -e "${YELLOW}Instalando Java 17...${NC}"
apt-get install -y openjdk-17-jdk
java -version

# Instalar Maven
echo -e "${YELLOW}Instalando Maven...${NC}"
apt-get install -y maven
mvn -version

# Instalar MySQL
echo -e "${YELLOW}Instalando MySQL...${NC}"
apt-get install -y mysql-server

# Configurar MySQL
echo -e "${YELLOW}Configurando MySQL...${NC}"
systemctl start mysql
systemctl enable mysql

# Crear base de datos y usuario
mysql -u root <<EOF
CREATE DATABASE IF NOT EXISTS recetas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'recetas_user'@'localhost' IDENTIFIED BY 'recetas_pass';
GRANT ALL PRIVILEGES ON recetas.* TO 'recetas_user'@'localhost';
FLUSH PRIVILEGES;
EOF

echo -e "${GREEN}MySQL configurado${NC}"

# Instalar Nginx (opcional, para reverse proxy)
echo -e "${YELLOW}Instalando Nginx...${NC}"
apt-get install -y nginx

# Configurar firewall
echo -e "${YELLOW}Configurando firewall...${NC}"
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 8080/tcp
ufw --force enable

# Crear usuario para la aplicación
echo -e "${YELLOW}Creando usuario de aplicación...${NC}"
if ! id "recetas" &>/dev/null; then
    useradd -m -s /bin/bash recetas
    echo -e "${GREEN}Usuario 'recetas' creado${NC}"
else
    echo -e "${YELLOW}Usuario 'recetas' ya existe${NC}"
fi

# Crear directorio de aplicación
APP_DIR="/opt/recetas"
mkdir -p $APP_DIR
chown recetas:recetas $APP_DIR

echo -e "${GREEN}=========================================="
echo -e "Configuración completada"
echo -e "==========================================${NC}"
echo ""
echo "Próximos pasos:"
echo "1. Copia el proyecto a: $APP_DIR"
echo "2. Ejecuta: ./scripts/deploy.sh"
echo "3. Inicia la aplicación"
echo ""

