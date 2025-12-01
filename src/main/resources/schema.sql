-- Tabla de roles
CREATE TABLE IF NOT EXISTS ROLE (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50) UNIQUE);

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS USERS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    address VARCHAR(255),
    phone VARCHAR(50),
    crops VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de relación usuario-roles
CREATE TABLE IF NOT EXISTS USER_ROLES (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES USERS(id),
    FOREIGN KEY (role_id) REFERENCES ROLE(id)
);

-- Tabla de recetas
CREATE TABLE IF NOT EXISTS recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    cuisine_type VARCHAR(100),
    ingredients VARCHAR(1000),
    country_origin VARCHAR(100),
    difficulty VARCHAR(50),
    cooking_time VARCHAR(50),
    instructions TEXT,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    view_count INTEGER DEFAULT 0,
    author_id BIGINT,
    FOREIGN KEY (author_id) REFERENCES USERS(id)
);
