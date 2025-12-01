# Guía de Despliegue en Máquina Virtual

Esta guía te ayudará a desplegar la aplicación Recetas Spring en una máquina virtual accesible públicamente.

## 📋 Requisitos Previos

- Máquina virtual con Ubuntu 20.04/22.04 o Debian 11/12
- Acceso SSH a la VM
- Permisos de administrador (sudo)
- IP pública configurada

## 🚀 Pasos de Despliegue

### 1. Configuración Inicial de la VM

```bash
# Conectarse a la VM
ssh usuario@IP_VM

# Ejecutar script de configuración
sudo bash scripts/setup-vm.sh
```

Este script instalará:
- Java 17
- Maven
- MySQL 8.0
- Nginx
- Configurará firewall

### 2. Subir el Proyecto a la VM

```bash
# Desde tu máquina local
scp -r /Users/user/Recetas-Spring usuario@IP_VM:/opt/recetas

# O clonar desde Git si está en un repositorio
cd /opt/recetas
git clone <tu-repositorio> .
```

### 3. Configurar Base de Datos

```bash
# Conectarse a MySQL
sudo mysql -u root

# Ejecutar script de base de datos
mysql -u root -p recetas < scripts-bbdd/schema-mysql.sql
```

O desde la aplicación, el script se ejecutará automáticamente si `spring.jpa.hibernate.ddl-auto=update` está configurado.

### 4. Configurar Aplicación

```bash
cd /opt/recetas

# Editar application.properties si es necesario
nano src/main/resources/application.properties

# Verificar configuración de base de datos:
# spring.datasource.url=jdbc:mysql://localhost:3306/recetas
# spring.datasource.username=root
# spring.datasource.password=root
```

### 5. Compilar y Desplegar

```bash
# Ejecutar script de despliegue
bash scripts/deploy.sh
```

Este script:
- Compila el proyecto
- Crea el JAR
- Configura directorios necesarios

### 6. Configurar Nginx (Opcional pero Recomendado)

```bash
# Copiar configuración de Nginx
sudo cp scripts/nginx-config.conf /etc/nginx/sites-available/recetas

# Crear enlace simbólico
sudo ln -s /etc/nginx/sites-available/recetas /etc/nginx/sites-enabled/

# Probar configuración
sudo nginx -t

# Reiniciar Nginx
sudo systemctl restart nginx
```

### 7. Configurar como Servicio Systemd (Recomendado)

```bash
# Copiar archivo de servicio
sudo cp scripts/systemd-service.service /etc/systemd/system/recetas.service

# Recargar systemd
sudo systemctl daemon-reload

# Habilitar servicio (inicia automáticamente al arrancar)
sudo systemctl enable recetas

# Iniciar servicio
sudo systemctl start recetas

# Verificar estado
sudo systemctl status recetas
```

### 8. Verificar Despliegue

```bash
# Verificar que la aplicación está corriendo
curl http://localhost:8080/recetas

# Ver logs
sudo journalctl -u recetas -f
```

### 9. Acceso Público

La aplicación debería estar accesible en:
- **Con Nginx**: `http://IP_VM/recetas`
- **Sin Nginx**: `http://IP_VM:8080/recetas`

## 🔧 Configuración de Firewall

Si usas un firewall en la VM:

```bash
# Permitir puertos necesarios
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 8080/tcp  # Aplicación (si no usas Nginx)
sudo ufw enable
```

## 🐳 Despliegue con Docker (Alternativa)

Si prefieres usar Docker:

```bash
# Construir imagen
docker-compose build

# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f
```

La aplicación estará disponible en `http://IP_VM:8080/recetas`

## 📝 Verificación Final

1. ✅ Aplicación inicia sin errores
2. ✅ Base de datos conectada
3. ✅ Página principal carga: `http://IP_VM/recetas`
4. ✅ Login funciona
5. ✅ APIs REST responden

## 🔍 Solución de Problemas

### La aplicación no inicia

```bash
# Ver logs del servicio
sudo journalctl -u recetas -n 50

# Verificar puerto
sudo netstat -tlnp | grep 8080

# Verificar Java
java -version
```

### Error de conexión a base de datos

```bash
# Verificar MySQL está corriendo
sudo systemctl status mysql

# Probar conexión
mysql -u root -p -e "SHOW DATABASES;"

# Verificar usuario
mysql -u root -p -e "SELECT User, Host FROM mysql.user;"
```

### Nginx no redirige correctamente

```bash
# Verificar configuración
sudo nginx -t

# Ver logs
sudo tail -f /var/log/nginx/recetas-error.log

# Reiniciar Nginx
sudo systemctl restart nginx
```

## 📞 Información de Contacto

Para problemas o dudas, revisa los logs de la aplicación:
- Systemd: `sudo journalctl -u recetas`
- Docker: `docker-compose logs`
- Nginx: `/var/log/nginx/recetas-error.log`

## 🔐 Seguridad

**IMPORTANTE**: Antes de poner en producción:

1. Cambiar contraseñas por defecto
2. Configurar SSL/TLS (HTTPS)
3. Restringir acceso a MySQL
4. Configurar firewall adecuadamente
5. Actualizar secretos JWT
6. Revisar configuración de seguridad

## 📦 Archivos Importantes

- `scripts/setup-vm.sh` - Configuración inicial
- `scripts/deploy.sh` - Script de despliegue
- `scripts/nginx-config.conf` - Configuración Nginx
- `scripts/systemd-service.service` - Servicio systemd
- `docker-compose.yml` - Configuración Docker
- `Dockerfile` - Imagen Docker

