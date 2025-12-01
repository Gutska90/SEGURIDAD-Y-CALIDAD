# 🚀 Guía Simplificada de Despliegue en Máquina Virtual

Esta guía te explica paso a paso cómo desplegar la aplicación Recetas Spring en una máquina virtual accesible públicamente.

---

## 📋 Requisitos Previos

Antes de comenzar, necesitas:

1. **Una máquina virtual** con:
   - Ubuntu 20.04/22.04 o Debian 11/12
   - Al menos 2GB de RAM
   - 10GB de espacio en disco
   - IP pública configurada

2. **Acceso SSH** a la VM con permisos de administrador (sudo)

3. **Puertos abiertos**:
   - Puerto 22 (SSH)
   - Puerto 80 (HTTP) o 8080 (Aplicación)

---

## 🔧 Paso 1: Preparar la Máquina Virtual

### 1.1 Conectarse a la VM

```bash
# Desde tu máquina local, conéctate por SSH
ssh usuario@IP_DE_TU_VM

# Ejemplo:
ssh ubuntu@192.168.1.100
```

### 1.2 Ejecutar Script de Configuración

El proyecto incluye un script que instala todo lo necesario:

```bash
# Subir el proyecto a la VM primero (ver paso 2)
# Luego ejecutar:
cd /opt/recetas
sudo bash scripts/setup-vm.sh
```

Este script instala automáticamente:
- ✅ Java 17
- ✅ Maven
- ✅ MySQL 8.0
- ✅ Nginx (servidor web)
- ✅ Configura el firewall básico

**Tiempo estimado**: 10-15 minutos

---

## 📦 Paso 2: Subir el Proyecto a la VM

Tienes dos opciones:

### Opción A: Usando SCP (Recomendado para primera vez)

```bash
# Desde tu máquina local
cd "/Users/user/Desktop/Semana 6 proyecto"

# Subir todo el proyecto
scp -r . usuario@IP_DE_TU_VM:/opt/recetas

# Ejemplo:
scp -r . ubuntu@192.168.1.100:/opt/recetas
```

### Opción B: Usando Git (Si tienes repositorio)

```bash
# En la VM
sudo mkdir -p /opt/recetas
cd /opt/recetas
sudo git clone https://github.com/tu-usuario/tu-repositorio.git .
```

---

## 🗄️ Paso 3: Configurar Base de Datos

### 3.1 Crear Base de Datos

```bash
# Conectarse a MySQL
sudo mysql -u root -p

# En el prompt de MySQL, ejecutar:
CREATE DATABASE recetas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'recetas_user'@'localhost' IDENTIFIED BY 'tu_password_seguro';
GRANT ALL PRIVILEGES ON recetas.* TO 'recetas_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 3.2 Ejecutar Scripts de Base de Datos

```bash
cd /opt/recetas

# Ejecutar script de esquema
mysql -u recetas_user -p recetas < scripts-bbdd/schema-mysql.sql

# Ejecutar datos iniciales (opcional)
mysql -u recetas_user -p recetas < src/main/resources/data.sql
```

---

## ⚙️ Paso 4: Configurar la Aplicación

### 4.1 Editar Configuración

```bash
cd /opt/recetas
nano src/main/resources/application.properties
```

Asegúrate de que tenga esta configuración:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/recetas
spring.datasource.username=recetas_user
spring.datasource.password=tu_password_seguro
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Puerto de la aplicación
server.port=8080

# Context path
server.servlet.context-path=/recetas
```

Guarda con `Ctrl+O`, Enter, y sal con `Ctrl+X`.

---

## 🔨 Paso 5: Compilar y Desplegar

### 5.1 Compilar el Proyecto

```bash
cd /opt/recetas

# Compilar y crear JAR
mvn clean package -DskipTests

# Esto creará: target/recetas-0.0.1-SNAPSHOT.jar
```

### 5.2 Crear Directorios Necesarios

```bash
# Crear directorio para uploads
sudo mkdir -p /opt/recetas/uploads/imagenes
sudo mkdir -p /opt/recetas/uploads/videos
sudo chown -R $USER:$USER /opt/recetas/uploads
```

---

## 🚀 Paso 6: Configurar como Servicio (Systemd)

Esto permite que la aplicación se inicie automáticamente al reiniciar la VM.

### 6.1 Crear Archivo de Servicio

```bash
sudo nano /etc/systemd/system/recetas.service
```

Pega este contenido:

```ini
[Unit]
Description=Recetas Spring Application
After=network.target mysql.service

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/recetas
ExecStart=/usr/bin/java -jar /opt/recetas/target/recetas-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**Nota**: Cambia `User=ubuntu` por tu usuario de la VM.

### 6.2 Activar el Servicio

```bash
# Recargar systemd
sudo systemctl daemon-reload

# Habilitar inicio automático
sudo systemctl enable recetas

# Iniciar el servicio
sudo systemctl start recetas

# Verificar que está corriendo
sudo systemctl status recetas
```

Si ves "active (running)" en verde, ¡está funcionando! ✅

---

## 🌐 Paso 7: Configurar Nginx (Opcional pero Recomendado)

Nginx actúa como proxy inverso y permite acceder a la app en el puerto 80.

### 7.1 Crear Configuración de Nginx

```bash
sudo nano /etc/nginx/sites-available/recetas
```

Pega este contenido:

```nginx
server {
    listen 80;
    server_name IP_DE_TU_VM;  # O tu dominio si tienes uno

    location /recetas {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Redirigir raíz a /recetas
    location = / {
        return 301 /recetas;
    }
}
```

**Importante**: Reemplaza `IP_DE_TU_VM` con la IP real de tu VM.

### 7.2 Activar Configuración

```bash
# Crear enlace simbólico
sudo ln -s /etc/nginx/sites-available/recetas /etc/nginx/sites-enabled/

# Probar configuración
sudo nginx -t

# Si dice "syntax is ok", reiniciar Nginx
sudo systemctl restart nginx
```

---

## 🔥 Paso 8: Configurar Firewall

```bash
# Permitir SSH (importante, no lo cierres)
sudo ufw allow 22/tcp

# Permitir HTTP
sudo ufw allow 80/tcp

# Si no usas Nginx, permitir puerto 8080
sudo ufw allow 8080/tcp

# Activar firewall
sudo ufw enable
```

---

## ✅ Paso 9: Verificar que Todo Funciona

### 9.1 Verificar que la Aplicación Está Corriendo

```bash
# Ver logs del servicio
sudo journalctl -u recetas -f

# O verificar puerto
sudo netstat -tlnp | grep 8080
```

### 9.2 Probar Acceso

Abre tu navegador y visita:

- **Con Nginx**: `http://IP_DE_TU_VM/recetas`
- **Sin Nginx**: `http://IP_DE_TU_VM:8080/recetas`

Deberías ver la página de inicio de la aplicación.

### 9.3 Probar Login

1. Ve a `http://IP_DE_TU_VM/recetas/login`
2. Usa uno de estos usuarios (de `data.sql`):
   - Usuario: `admin` / Password: `admin` (ADMIN)
   - Usuario: `juan` / Password: `juan` (USER)
   - Usuario: `maria` / Password: `maria` (USER)

---

## 🐛 Solución de Problemas Comunes

### La aplicación no inicia

```bash
# Ver logs detallados
sudo journalctl -u recetas -n 100

# Verificar Java
java -version  # Debe ser Java 17

# Verificar que el JAR existe
ls -lh /opt/recetas/target/*.jar
```

### Error de conexión a base de datos

```bash
# Verificar MySQL está corriendo
sudo systemctl status mysql

# Probar conexión
mysql -u recetas_user -p -e "SHOW DATABASES;"

# Verificar usuario tiene permisos
mysql -u root -p -e "SHOW GRANTS FOR 'recetas_user'@'localhost';"
```

### Nginx no redirige correctamente

```bash
# Verificar configuración
sudo nginx -t

# Ver logs de error
sudo tail -f /var/log/nginx/error.log

# Reiniciar Nginx
sudo systemctl restart nginx
```

### No puedo acceder desde fuera

```bash
# Verificar firewall
sudo ufw status

# Verificar que el puerto está abierto
sudo netstat -tlnp | grep 80
sudo netstat -tlnp | grep 8080

# Verificar en el proveedor de la VM que los puertos están abiertos
# (AWS Security Groups, Azure NSG, etc.)
```

---

## 📝 Comandos Útiles

```bash
# Reiniciar aplicación
sudo systemctl restart recetas

# Ver logs en tiempo real
sudo journalctl -u recetas -f

# Detener aplicación
sudo systemctl stop recetas

# Iniciar aplicación
sudo systemctl start recetas

# Ver estado
sudo systemctl status recetas

# Ver logs de Nginx
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

---

## 🔐 Seguridad (IMPORTANTE)

Antes de poner en producción:

1. ✅ Cambiar contraseñas por defecto
2. ✅ Configurar SSL/TLS (HTTPS) con Let's Encrypt
3. ✅ Restringir acceso a MySQL (solo localhost)
4. ✅ Actualizar secretos JWT en `application.properties`
5. ✅ Configurar firewall adecuadamente
6. ✅ Hacer backups regulares de la base de datos

---

## 📞 Resumen Rápido

```bash
# 1. Conectarse a VM
ssh usuario@IP_VM

# 2. Subir proyecto
scp -r . usuario@IP_VM:/opt/recetas

# 3. Configurar VM
cd /opt/recetas
sudo bash scripts/setup-vm.sh

# 4. Configurar BD
sudo mysql -u root -p
# (crear BD y usuario)

# 5. Compilar
mvn clean package -DskipTests

# 6. Configurar servicio
sudo nano /etc/systemd/system/recetas.service
sudo systemctl enable recetas
sudo systemctl start recetas

# 7. Configurar Nginx (opcional)
sudo nano /etc/nginx/sites-available/recetas
sudo ln -s /etc/nginx/sites-available/recetas /etc/nginx/sites-enabled/
sudo systemctl restart nginx

# 8. Verificar
curl http://localhost:8080/recetas
```

---

## ✅ Checklist Final

- [ ] VM configurada con Java, Maven, MySQL, Nginx
- [ ] Proyecto subido a `/opt/recetas`
- [ ] Base de datos creada y configurada
- [ ] `application.properties` configurado
- [ ] Proyecto compilado (JAR creado)
- [ ] Servicio systemd configurado y corriendo
- [ ] Nginx configurado (si se usa)
- [ ] Firewall configurado
- [ ] Aplicación accesible en `http://IP_VM/recetas`
- [ ] Login funciona correctamente

---

**¡Listo! Tu aplicación debería estar funcionando en `http://IP_DE_TU_VM/recetas`** 🎉

