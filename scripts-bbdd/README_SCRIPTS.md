# Scripts de Base de Datos

Este directorio contiene los scripts SQL para crear la base de datos de la aplicación Recetas.

## Scripts Disponibles

### 1. `schema-mysql.sql`
Script para **MySQL 5.7+** o **MariaDB 10.2+**

**Características:**
- Usa `AUTO_INCREMENT` para IDs
- Tipos de datos: `BIGINT`, `VARCHAR`, `TEXT`, `TIMESTAMP`
- Sintaxis MySQL estándar

**Ejecución:**
```bash
mysql -u root -p < scripts-bbdd/schema-mysql.sql
```

O desde MySQL:
```sql
SOURCE scripts-bbdd/schema-mysql.sql;
```

### 2. `schema-oracle.sql`
Script para **Oracle Database 11g+**

**Características:**
- Usa secuencias y triggers para auto-incrementar IDs
- Tipos de datos: `NUMBER`, `VARCHAR2`, `CLOB`, `TIMESTAMP`
- Sintaxis Oracle PL/SQL
- Incluye triggers para auto-incrementar

**Ejecución:**
```bash
sqlplus usuario/password@servidor @scripts-bbdd/schema-oracle.sql
```

O desde SQL*Plus:
```sql
@scripts-bbdd/schema-oracle.sql
```

O desde SQL Developer:
1. Abrir el archivo `schema-oracle.sql`
2. Ejecutar todo el script (F5)

## Estructura de la Base de Datos

### Tablas Principales:
1. **roles** - Roles de usuario (ROLE_USER, ROLE_ADMIN)
2. **usuarios** - Usuarios del sistema
3. **usuario_roles** - Relación muchos a muchos usuarios-roles
4. **recetas** - Recetas publicadas
5. **anuncios** - Anuncios publicitarios

### Tablas Nuevas (Semana 5):
6. **comentarios** - Comentarios en recetas
7. **valoraciones** - Valoraciones de recetas (1-5 estrellas)
8. **receta_fotos** - Fotos asociadas a recetas
9. **receta_videos** - Videos asociados a recetas
10. **receta_compartidas** - Registro de compartidos de recetas

## Datos de Prueba

Ambos scripts incluyen:
- 2 roles: `ROLE_USER`, `ROLE_ADMIN`
- 3 usuarios de prueba:
  - `admin` (ROLE_ADMIN) - password: `password123`
  - `chef` (ROLE_USER) - password: `password123`
  - `usuario` (ROLE_USER) - password: `password123`
- 6 recetas de ejemplo
- 3 anuncios de ejemplo

**⚠️ IMPORTANTE:** Las contraseñas en los scripts son placeholders. En producción, usar contraseñas reales encriptadas con BCrypt.

## Configuración de la Aplicación

### Para MySQL:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/recetas?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### Para Oracle:
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=recetas_user
spring.datasource.password=recetas_pass
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
```

## Diferencias entre MySQL y Oracle

| Característica | MySQL | Oracle |
|---------------|-------|--------|
| Auto-increment | `AUTO_INCREMENT` | Secuencias + Triggers |
| Tipos numéricos | `BIGINT` | `NUMBER(19)` |
| Tipos texto | `VARCHAR`, `TEXT` | `VARCHAR2`, `CLOB` |
| Booleanos | `TINYINT(1)` | `NUMBER(1)` |
| Constraints | `CHECK` | `CHECK` |
| Foreign Keys | `ON DELETE CASCADE` | `ON DELETE CASCADE` |
| Unique | `UNIQUE KEY` | `UNIQUE` constraint |

## Verificación

### MySQL:
```sql
SHOW TABLES;
SELECT COUNT(*) FROM usuarios;
SELECT COUNT(*) FROM recetas;
```

### Oracle:
```sql
SELECT table_name FROM user_tables;
SELECT COUNT(*) FROM usuarios;
SELECT COUNT(*) FROM recetas;
```

## Solución de Problemas

### MySQL:
- **Error de charset:** Asegurarse de usar `utf8mb4`
- **Error de permisos:** Verificar que el usuario tenga permisos CREATE

### Oracle:
- **Error de secuencia:** Verificar que las secuencias se crearon correctamente
- **Error de trigger:** Verificar que los triggers se compilaron sin errores
- **Error de permisos:** Verificar que el usuario tenga permisos CREATE TABLE, CREATE SEQUENCE, CREATE TRIGGER

## Notas Adicionales

1. **Backup:** Siempre hacer backup antes de ejecutar scripts en producción
2. **Contraseñas:** Cambiar las contraseñas de los usuarios de prueba en producción
3. **Índices:** Los scripts no incluyen índices adicionales. Considerar agregarlos según necesidades de rendimiento
4. **Particiones:** Para grandes volúmenes de datos, considerar particionar tablas grandes

