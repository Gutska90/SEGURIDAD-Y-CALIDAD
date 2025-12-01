# Recetas Spring - Aplicación Web de Recetas

Aplicación web desarrollada con Spring Boot para compartir y gestionar recetas culinarias.

## 🚀 Características

- **Gestión de Recetas**: Crear, buscar y visualizar recetas
- **Sistema de Usuarios**: Registro, login y gestión de usuarios (admin)
- **Comentarios y Valoraciones**: Los usuarios pueden comentar y valorar recetas
- **Multimedia**: Subir fotos y videos a las recetas
- **Compartir**: Compartir recetas en redes sociales
- **Seguridad**: Autenticación JWT y protección de endpoints

## 📋 Requisitos

- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+
- (Opcional) Docker y Docker Compose

## 🔧 Instalación Local

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd Recetas-Spring
```

### 2. Configurar Base de Datos

```bash
# Crear base de datos
mysql -u root -p < scripts-bbdd/schema-mysql.sql
```

### 3. Configurar aplicación

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/recetas
spring.datasource.username=root
spring.datasource.password=tu_password
```

### 4. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080/recetas`

## 🐳 Despliegue con Docker

```bash
# Construir y ejecutar
docker-compose up -d

# Ver logs
docker-compose logs -f
```

## 🌐 Despliegue en Máquina Virtual

Ver la [Guía de Despliegue en VM](GUIA_DESPLIEGUE_VM.md) para instrucciones detalladas.

Resumen rápido:

```bash
# En la VM
sudo bash scripts/setup-vm.sh
bash scripts/deploy.sh
sudo systemctl start recetas
```

## 📚 Estructura del Proyecto

```
Recetas-Spring/
├── src/
│   ├── main/
│   │   ├── java/com/recetas/recetas/
│   │   │   ├── config/          # Configuración (Security, JWT, etc.)
│   │   │   ├── controller/       # Controladores REST y Web
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── repository/      # Repositorios JPA
│   │   │   ├── service/         # Lógica de negocio
│   │   │   └── util/            # Utilidades
│   │   └── resources/
│   │       ├── templates/       # Plantillas Thymeleaf
│   │       └── static/          # CSS, JS, imágenes
│   └── test/                    # Pruebas unitarias
├── scripts/                     # Scripts de despliegue
├── scripts-bbdd/                # Scripts de base de datos
└── documentacion/               # Documentación técnica
```

## 🔐 APIs REST

### Públicas

- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/registro` - Registrar nuevo usuario

### Privadas (requieren JWT)

- `GET /api/usuarios` - Listar usuarios (ADMIN)
- `POST /api/usuarios` - Crear usuario (ADMIN)
- `GET /api/usuarios/{id}` - Obtener usuario (ADMIN)
- `PUT /api/usuarios/{id}` - Actualizar usuario (ADMIN)
- `DELETE /api/usuarios/{id}` - Eliminar usuario (ADMIN)
- `POST /api/recetas` - Crear receta
- `POST /api/comentarios` - Crear comentario
- `POST /api/valoraciones` - Crear/actualizar valoración
- `POST /api/compartir` - Compartir receta
- `POST /api/media/foto` - Subir foto
- `POST /api/media/video` - Subir video

## 🧪 Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar prueba específica
mvn test -Dtest=UsuarioServiceTest
```

## 📖 Documentación

- [Guía de Despliegue en VM](GUIA_DESPLIEGUE_VM.md)
- [Documentación Técnica](documentacion/README.md)
- [Scripts de Base de Datos](scripts-bbdd/README_SCRIPTS.md)

## 🛠️ Tecnologías

- **Backend**: Spring Boot 3.5.7
- **Seguridad**: Spring Security + JWT
- **Base de Datos**: MySQL 8.0
- **Frontend**: Thymeleaf + JavaScript
- **Build**: Maven
- **Testing**: JUnit 5, Mockito

## 📝 Licencia

Este proyecto es parte de un curso académico.

## 👥 Autores

Equipo de desarrollo - ISY2202
