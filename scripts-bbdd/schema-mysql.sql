-- Script SQL para MySQL
-- Base de datos: recetas

CREATE DATABASE IF NOT EXISTS recetas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE recetas;

-- Eliminar tablas si existen (en orden inverso de dependencias)
DROP TABLE IF EXISTS receta_compartidas;
DROP TABLE IF EXISTS receta_videos;
DROP TABLE IF EXISTS receta_fotos;
DROP TABLE IF EXISTS valoraciones;
DROP TABLE IF EXISTS comentarios;
DROP TABLE IF EXISTS usuario_roles;
DROP TABLE IF EXISTS anuncios;
DROP TABLE IF EXISTS recetas;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS roles;

-- Crear tabla roles
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

-- Crear tabla usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(200) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    enabled TINYINT(1) DEFAULT 1 CHECK (enabled IN (0,1))
);

-- Crear tabla usuario_roles
CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, role_id),
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Crear tabla recetas
CREATE TABLE recetas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    tipo_cocina VARCHAR(100),
    pais_origen VARCHAR(100),
    dificultad VARCHAR(50),
    tiempo_coccion INT,
    descripcion TEXT,
    ingredientes TEXT,
    instrucciones TEXT,
    imagen_url VARCHAR(500),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    popularidad INT DEFAULT 0
);

-- Crear tabla anuncios
CREATE TABLE anuncios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa VARCHAR(200) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    imagen_url VARCHAR(500),
    url_destino VARCHAR(500),
    activo TINYINT(1) DEFAULT 1 CHECK (activo IN (0,1))
);

-- Crear tabla comentarios
CREATE TABLE comentarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receta_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    comentario TEXT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comentario_receta FOREIGN KEY (receta_id) REFERENCES recetas(id) ON DELETE CASCADE,
    CONSTRAINT fk_comentario_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Crear tabla valoraciones
CREATE TABLE valoraciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receta_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    puntuacion INT NOT NULL CHECK (puntuacion >= 1 AND puntuacion <= 5),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NULL,
    CONSTRAINT fk_valoracion_receta FOREIGN KEY (receta_id) REFERENCES recetas(id) ON DELETE CASCADE,
    CONSTRAINT fk_valoracion_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    UNIQUE KEY unique_valoracion (receta_id, usuario_id)
);

-- Crear tabla receta_fotos
CREATE TABLE receta_fotos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receta_id BIGINT NOT NULL,
    url_foto VARCHAR(500) NOT NULL,
    nombre_archivo VARCHAR(255),
    tipo_archivo VARCHAR(50),
    tamaño_archivo BIGINT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    es_principal TINYINT(1) DEFAULT 0,
    CONSTRAINT fk_foto_receta FOREIGN KEY (receta_id) REFERENCES recetas(id) ON DELETE CASCADE
);

-- Crear tabla receta_videos
CREATE TABLE receta_videos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receta_id BIGINT NOT NULL,
    url_video VARCHAR(500) NOT NULL,
    nombre_archivo VARCHAR(255),
    tipo_archivo VARCHAR(50),
    tamaño_archivo BIGINT,
    duracion_segundos INT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_video_receta FOREIGN KEY (receta_id) REFERENCES recetas(id) ON DELETE CASCADE
);

-- Crear tabla receta_compartidas
CREATE TABLE receta_compartidas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receta_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    plataforma VARCHAR(50),
    fecha_compartido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_compartida_receta FOREIGN KEY (receta_id) REFERENCES recetas(id) ON DELETE CASCADE,
    CONSTRAINT fk_compartida_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Insertar roles
INSERT INTO roles (nombre) VALUES ('ROLE_USER');
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN');

-- Insertar usuarios de prueba (password: password123)
INSERT INTO usuarios (nombre_completo, username, password, email, enabled) VALUES 
('Administrador del Sistema', 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyY5Y5Y5Y5Y5', 'admin@recetas.com', 1);

INSERT INTO usuarios (nombre_completo, username, password, email, enabled) VALUES 
('Chef Profesional', 'chef', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyY5Y5Y5Y5Y5', 'chef@recetas.com', 1);

INSERT INTO usuarios (nombre_completo, username, password, email, enabled) VALUES 
('Usuario Común', 'usuario', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyY5Y5Y5Y5Y5', 'usuario@recetas.com', 1);

-- Asignar roles a usuarios
INSERT INTO usuario_roles (usuario_id, role_id) VALUES (1, 2); -- admin tiene ROLE_ADMIN
INSERT INTO usuario_roles (usuario_id, role_id) VALUES (2, 1); -- chef tiene ROLE_USER
INSERT INTO usuario_roles (usuario_id, role_id) VALUES (3, 1); -- usuario tiene ROLE_USER

-- Insertar recetas de ejemplo
INSERT INTO recetas (nombre, tipo_cocina, pais_origen, dificultad, tiempo_coccion, descripcion, ingredientes, instrucciones, imagen_url, popularidad) VALUES
('Paella Valenciana', 'Mediterránea', 'España', 'Media', 60, 'Plato tradicional español con arroz, pollo y mariscos', 
'Arroz, pollo, mariscos, azafrán, pimientos, judías verdes, tomate, aceite de oliva', 
'1. Sofreír el pollo\n2. Agregar verduras\n3. Añadir el arroz y caldo\n4. Cocinar por 20 minutos\n5. Dejar reposar', 
'https://images.unsplash.com/photo-1534080564583-6be75777b70a', 95);

INSERT INTO recetas (nombre, tipo_cocina, pais_origen, dificultad, tiempo_coccion, descripcion, ingredientes, instrucciones, imagen_url, popularidad) VALUES
('Tacos al Pastor', 'Mexicana', 'México', 'Media', 45, 'Tacos con carne marinada en especias mexicanas', 
'Carne de cerdo, piña, cebolla, cilantro, tortillas, especias mexicanas, limón', 
'1. Marinar la carne\n2. Asar la carne\n3. Cortar en trozos pequeños\n4. Servir en tortillas con piña, cebolla y cilantro', 
'https://images.unsplash.com/photo-1565299585323-38d6b0865b47', 88);

INSERT INTO recetas (nombre, tipo_cocina, pais_origen, dificultad, tiempo_coccion, descripcion, ingredientes, instrucciones, imagen_url, popularidad) VALUES
('Sushi Roll California', 'Japonesa', 'Japón', 'Difícil', 30, 'Rollo de sushi con cangrejo, aguacate y pepino', 
'Arroz para sushi, alga nori, cangrejo, aguacate, pepino, vinagre de arroz, wasabi', 
'1. Cocinar el arroz\n2. Extender el alga nori\n3. Colocar arroz y relleno\n4. Enrollar con bambú\n5. Cortar en porciones', 
'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351', 92);

-- Insertar anuncios
INSERT INTO anuncios (empresa, titulo, descripcion, imagen_url, url_destino, activo) VALUES
('Utensilios Gourmet', '¡Descuento 30% en Ollas!', 'Las mejores ollas profesionales para tu cocina', 
'https://images.unsplash.com/photo-1556911220-bff31c812dba', '#', 1);

INSERT INTO anuncios (empresa, titulo, descripcion, imagen_url, url_destino, activo) VALUES
('Supermercado Fresh', 'Ingredientes Frescos Diarios', 'Los mejores ingredientes para tus recetas', 
'https://images.unsplash.com/photo-1542838132-92c53300491e', '#', 1);

