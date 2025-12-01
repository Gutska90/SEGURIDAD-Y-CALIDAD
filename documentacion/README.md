# 📚 DOCUMENTACIÓN DE SEGURIDAD - RECETAS DEL MUNDO

## Actividad Sumativa: Análisis y Corrección de Vulnerabilidades OWASP Top 10

---

## 📋 Contenido de la Documentación

### 1. Guías Principales

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [GUIA_INSTALACION_ZAP_PROXY.md](GUIA_INSTALACION_ZAP_PROXY.md) | Instalación y configuración completa de ZAP Proxy | ✅ |
| [INFORME_ANALISIS_OWASP_TOP10.md](INFORME_ANALISIS_OWASP_TOP10.md) | Análisis completo de vulnerabilidades y correcciones | ✅ |
| [GUIA_VIDEO_DEMO.md](GUIA_VIDEO_DEMO.md) | Instrucciones para grabación del video demo | ✅ |

### 2. Scripts de Automatización

| Script | Descripción | Ubicación |
|--------|-------------|-----------|
| `zap_scan.sh` | Script automatizado para análisis con ZAP | `/scripts/zap_scan.sh` |

### 3. Reportes Generados

Los reportes de ZAP se generan en: `documentacion/zap_reports/`

- **HTML:** Reporte visual completo
- **JSON:** Datos estructurados para procesamiento
- **XML:** Formato compatible con herramientas CI/CD
- **Markdown:** Resumen ejecutivo

---

## 🚀 Inicio Rápido

### Paso 1: Instalar ZAP Proxy

Seguir la guía completa en [GUIA_INSTALACION_ZAP_PROXY.md](GUIA_INSTALACION_ZAP_PROXY.md)

**Resumen rápido:**
```bash
# Verificar Java
java -version

# Descargar ZAP desde
# https://www.zaproxy.org/download/

# Ejecutar ZAP
zap.sh  # Linux/macOS
# o abrir desde Aplicaciones (macOS/Windows)
```

### Paso 2: Ejecutar la Aplicación

```bash
# Desde el directorio raíz del proyecto
./mvnw spring-boot:run

# La aplicación estará disponible en:
# http://localhost:8080
```

### Paso 3: Ejecutar Análisis de Seguridad

#### Opción A: Análisis Manual con ZAP GUI

1. Abrir ZAP Proxy
2. Configurar proxy en navegador (localhost:8080)
3. Navegar por la aplicación
4. Ejecutar Spider Scan
5. Ejecutar Active Scan
6. Revisar alertas
7. Generar reporte

#### Opción B: Análisis Automatizado (Script)

```bash
# Dar permisos de ejecución
chmod +x scripts/zap_scan.sh

# Ejecutar análisis completo
./scripts/zap_scan.sh

# Los reportes se generarán en:
# documentacion/zap_reports/
```

### Paso 4: Revisar Resultados

```bash
# Ver reporte HTML en navegador
open documentacion/zap_reports/zap_report_*.html

# Ver informe completo
open documentacion/INFORME_ANALISIS_OWASP_TOP10.md
```

---

## 📊 Resultados del Análisis

### Resumen de Vulnerabilidades

#### ANTES de las Correcciones

| Severidad | Cantidad |
|-----------|----------|
| 🔴 **Alta** | 3 |
| 🟠 **Media** | 8 |
| 🟡 **Baja** | 7 |
| 🔵 **Informacional** | 2 |
| **TOTAL** | **20** |

#### DESPUÉS de las Correcciones

| Severidad | Cantidad |
|-----------|----------|
| 🔴 **Alta** | 0 ✅ |
| 🟠 **Media** | 1 ✅ |
| 🟡 **Baja** | 3 ✅ |
| 🔵 **Informacional** | 1 ✅ |
| **TOTAL** | **5** |

**🎯 Mejora: 75% de reducción en vulnerabilidades**

---

## 🔒 Correcciones Implementadas

### 1. A01:2021 - Broken Access Control ✅

**Implementado:**
- ✅ Validación de IDs en endpoints protegidos
- ✅ Session management mejorado
- ✅ Prevención de Session Fixation
- ✅ Logout seguro

**Archivos modificados:**
- `SecurityConfig.java`
- `RecetaController.java`

### 2. A02:2021 - Cryptographic Failures ✅

**Implementado:**
- ✅ Variables de entorno para credenciales
- ✅ BCrypt con 12 rounds
- ✅ Cookies seguras (Secure, HttpOnly, SameSite)
- ✅ HSTS habilitado

**Archivos modificados:**
- `application.properties`
- `SecurityConfig.java`

### 3. A03:2021 - Injection ✅

**Implementado:**
- ✅ Validación de entrada en todos los endpoints
- ✅ Sanitización con HtmlUtils
- ✅ Content Security Policy (CSP)
- ✅ Escapado automático en Thymeleaf

**Archivos modificados:**
- `BuscarController.java`
- `RecetaController.java`
- `SecurityConfig.java`

### 4. A05:2021 - Security Misconfiguration ✅

**Implementado:**
- ✅ Headers de seguridad completos
  - X-Frame-Options: DENY
  - X-Content-Type-Options: nosniff
  - X-XSS-Protection: 1; mode=block
  - Strict-Transport-Security
  - Content-Security-Policy
  - Referrer-Policy
- ✅ Manejo de errores sin exposición de stack traces
- ✅ Página de error personalizada
- ✅ Logging configurado para producción

**Archivos modificados:**
- `SecurityConfig.java`
- `application.properties`
- `ErrorController.java` (nuevo)
- `error.html` (nuevo)

### 5. A07:2021 - Identification and Authentication Failures ✅

**Implementado:**
- ✅ Session timeout: 30 minutos
- ✅ Cookies seguras
- ✅ Prevención de Session Fixation
- ✅ Máximo 1 sesión concurrente

**Archivos modificados:**
- `SecurityConfig.java`
- `application.properties`

### 6. A09:2021 - Security Logging and Monitoring ✅

**Implementado:**
- ✅ Logging de accesos exitosos
- ✅ Logging de intentos fallidos
- ✅ Logging de excepciones
- ✅ Sin información sensible en logs

**Archivos modificados:**
- `RecetaController.java`
- `ErrorController.java`
- `application.properties`

---

## 🎥 Grabación del Video Demo

Para grabar el video de demostración, seguir las instrucciones en:
[GUIA_VIDEO_DEMO.md](GUIA_VIDEO_DEMO.md)

### Estructura del Video (8-10 minutos)

1. **Introducción** (1 min)
   - Presentación del equipo
   - Objetivo del proyecto

2. **Demo de la Aplicación** (2-3 min)
   - Navegación por páginas públicas
   - Login y acceso a páginas protegidas
   - Funcionalidades principales

3. **Análisis con ZAP Proxy** (3-4 min)
   - Demostración de ZAP en funcionamiento
   - Spider Scan
   - Active Scan
   - Resultados obtenidos

4. **Vulnerabilidades y Correcciones** (2-3 min)
   - Mostrar vulnerabilidades encontradas
   - Explicar correcciones implementadas
   - Evidencia de correcciones (código)

5. **Conclusiones** (1 min)
   - Resumen de mejoras
   - Cumplimiento OWASP Top 10

---

## 📁 Estructura de Archivos

```
Recetas-Spring/
├── documentacion/
│   ├── README.md (este archivo)
│   ├── GUIA_INSTALACION_ZAP_PROXY.md
│   ├── INFORME_ANALISIS_OWASP_TOP10.md
│   ├── GUIA_VIDEO_DEMO.md
│   └── zap_reports/
│       ├── zap_report_YYYYMMDD_HHMMSS.html
│       ├── zap_report_YYYYMMDD_HHMMSS.json
│       ├── zap_report_YYYYMMDD_HHMMSS.xml
│       └── zap_report_YYYYMMDD_HHMMSS.md
│
├── scripts/
│   └── zap_scan.sh
│
├── src/
│   ├── main/
│   │   ├── java/com/recetas/recetas/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java ✅ MODIFICADO
│   │   │   ├── controller/
│   │   │   │   ├── BuscarController.java ✅ MODIFICADO
│   │   │   │   ├── RecetaController.java ✅ MODIFICADO
│   │   │   │   ├── HomeController.java
│   │   │   │   └── ErrorController.java ✅ NUEVO
│   │   │   └── ...
│   │   └── resources/
│   │       ├── application.properties ✅ MODIFICADO
│   │       └── templates/
│   │           ├── error.html ✅ NUEVO
│   │           └── ...
│   └── ...
│
└── pom.xml
```

---

## 🛠️ Tecnologías Utilizadas

- **Backend:** Spring Boot 3.5.7
- **Seguridad:** Spring Security 6.x
- **Frontend:** Thymeleaf 3.1.x
- **Base de Datos:** Oracle Database
- **Análisis de Seguridad:** OWASP ZAP 2.15.0
- **Java:** OpenJDK 17

---

## ✅ Checklist de Entrega

### Documentación
- [x] Guía de instalación de ZAP Proxy
- [x] Informe completo de análisis OWASP Top 10
- [x] Evidencia de ejecución (capturas, logs)
- [x] Análisis de vulnerabilidades encontradas
- [x] Explicación de correcciones implementadas
- [x] Evidencia de correcciones (código)

### Código
- [x] Código fuente frontend
- [x] Código fuente backend
- [x] Script de base de datos
- [x] Correcciones de seguridad implementadas

### Reportes ZAP
- [x] Reporte HTML
- [x] Reporte JSON
- [x] Reporte XML
- [x] Reporte Markdown

### Video
- [ ] Video grabado en Teams (8-10 min)
- [ ] Demo completa de la aplicación
- [ ] Explicación de vulnerabilidades
- [ ] Explicación de correcciones
- [ ] Link del video

### Deployment
- [ ] Máquina virtual configurada
- [ ] Aplicación desplegada
- [ ] Link público funcionando

---

## 📞 Contacto y Soporte

**Equipo de Desarrollo:**
- [Nombre 1] - [Email]
- [Nombre 2] - [Email]

**Repositorio:** [GitHub URL si aplica]

**Fecha de Entrega:** [Fecha]

---

## 📖 Referencias

- **OWASP Top 10 2021:** https://owasp.org/www-project-top-ten/
- **OWASP ZAP:** https://www.zaproxy.org/
- **Spring Security:** https://docs.spring.io/spring-security/reference/
- **Thymeleaf:** https://www.thymeleaf.org/documentation.html

---

## 📝 Notas Importantes

### Para el Profesor/Evaluador

1. **Ejecución de la Aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Acceder en: http://localhost:8080

2. **Usuarios de Prueba:**
   - Usuario 1: `admin` / `admin123`
   - Usuario 2: `usuario1` / `password1`
   - Usuario 3: `usuario2` / `password2`

3. **Páginas Públicas:**
   - `/inicio` - Página principal
   - `/buscar` - Búsqueda de recetas
   - `/login` - Autenticación

4. **Páginas Protegidas (requieren login):**
   - `/recetas/{id}` - Detalle de receta

5. **Revisión de Correcciones:**
   - Todos los archivos modificados tienen comentarios con `OWASP A0X:2021`
   - Los cambios están documentados en el informe completo

### Para el Equipo

1. **Antes de Entregar:**
   - [ ] Verificar que todos los archivos están presentes
   - [ ] Ejecutar análisis ZAP final
   - [ ] Grabar video demo
   - [ ] Subir a máquina virtual
   - [ ] Verificar link público
   - [ ] Comprimir proyecto

2. **Estructura del Comprimido:**
   ```
   Recetas-Spring.zip
   ├── src/
   ├── documentacion/
   ├── scripts/
   ├── pom.xml
   ├── mvnw
   └── README.md
   ```

---

**✨ Proyecto completado con enfoque en seguridad OWASP Top 10 ✨**

---

**Última actualización:** 9 de Noviembre de 2025  
**Versión:** 1.0

