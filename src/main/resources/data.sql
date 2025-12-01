-- Roles
INSERT INTO ROLE(name) VALUES ('ROLE_USER');
INSERT INTO ROLE(name) VALUES ('ROLE_ADMIN');

-- 3 usuarios de prueba (password igual al username para desarrollo)
INSERT INTO USERS(id, name, username, email, password, enabled, address, phone, crops) VALUES
 (1,'Admin','admin','admin@recetas.cl','admin',TRUE,'Santiago','+56 9 1111 1111','Trigo'),
 (2,'Juan Perez','juan','juan@recetas.cl','juan',TRUE,'Rancagua','+56 9 2222 2222','Maíz'),
 (3,'Maria Lopez','maria','maria@recetas.cl','maria',TRUE,'Talca','+56 9 3333 3333','Papás');

-- Asignar roles
INSERT INTO USER_ROLES(user_id, role_id) VALUES (1, 2); -- admin -> ROLE_ADMIN
INSERT INTO USER_ROLES(user_id, role_id) VALUES (2, 1); -- juan -> ROLE_USER
INSERT INTO USER_ROLES(user_id, role_id) VALUES (3, 1); -- maria -> ROLE_USER

-- Recetas de ejemplo para la aplicación
INSERT INTO recipes(name, cuisine_type, ingredients, country_origin, difficulty, cooking_time, instructions, image_url, view_count, author_id) VALUES
('Pasta Carbonara', 'Italiana', 'Pasta, Panceta, Huevos, Queso Parmesano, Pimienta negra', 'Italia', 'Media', '30 minutos', 
 '1. Cocer la pasta al dente. 2. Freír la panceta hasta que esté crujiente. 3. Mezclar huevos con queso parmesano. 4. Combinar todo con la pasta caliente. 5. Servir inmediatamente.',
 'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=800', 150, 2),

('Paella Valenciana', 'Española', 'Arroz, Pollo, Conejo, Judías verdes, Azafrán, Tomate', 'España', 'Difícil', '60 minutos',
 '1. Preparar el sofrito con tomate. 2. Añadir carnes y cocer. 3. Agregar arroz y caldo. 4. Cocer a fuego medio-bajo. 5. Dejar reposar antes de servir.',
 'https://images.unsplash.com/photo-1556911220-bff31c812dee?w=800', 200, 3),

('Sushi Maki', 'Japonesa', 'Arroz para sushi, Alga nori, Salmón, Aguacate, Wasabi, Salsa de soja', 'Japón', 'Difícil', '45 minutos',
 '1. Cocer el arroz para sushi. 2. Extender arroz sobre alga nori. 3. Colocar rellenos. 4. Enrollar firmemente. 5. Cortar en piezas.',
 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=800', 180, 2),

('Tacos al Pastor', 'Mexicana', 'Carne de cerdo, Piña, Cebolla, Cilantro, Tortillas, Limón', 'México', 'Fácil', '40 minutos',
 '1. Marinar la carne con especias. 2. Asar la carne lentamente. 3. Cortar en trozos finos. 4. Servir en tortillas calientes. 5. Acompañar con piña y cebolla.',
 'https://images.unsplash.com/photo-1565299585323-381dd0ac40c5?w=800', 250, 1),

('Coq au Vin', 'Francesa', 'Pollo, Vino tinto, Champiñones, Tocino, Cebollas pequeñas', 'Francia', 'Media', '90 minutos',
 '1. Marinar el pollo en vino. 2. Dorar el pollo con tocino. 3. Cocinar con vino y verduras. 4. Reducir la salsa. 5. Servir con guarnición.',
 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=800', 120, 3),

('Pad Thai', 'Tailandesa', 'Fideos de arroz, Camarones, Huevos, Brotes de soja, Cacahuetes, Lima', 'Tailandia', 'Media', '25 minutos',
 '1. Remojar fideos de arroz. 2. Saltear camarones. 3. Añadir huevos y fideos. 4. Agregar salsa y verduras. 5. Servir con cacahuetes y lima.',
 'https://images.unsplash.com/photo-1559314809-0d155014e29e?w=800', 170, 2),

('Ceviche Peruano', 'Peruana', 'Pescado blanco, Limón, Cebolla roja, Ají, Cilantro, Camote', 'Perú', 'Fácil', '20 minutos',
 '1. Cortar pescado en cubos. 2. Macerar en limón. 3. Agregar cebolla y ají. 4. Dejar reposar 10 minutos. 5. Servir fresco con cilantro.',
 'https://images.unsplash.com/photo-1580847724104-b11059e1674e?w=800', 220, 1),

('Hamburguesa Clásica', 'Americana', 'Carne molida, Pan de hamburguesa, Lechuga, Tomate, Queso, Cebolla', 'Estados Unidos', 'Fácil', '15 minutos',
 '1. Formar hamburguesas de carne. 2. Cocinar a la parrilla o sartén. 3. Asar el pan. 4. Montar con ingredientes. 5. Servir caliente.',
 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800', 300, 2),

('Ramen Tradicional', 'Japonesa', 'Fideos ramen, Caldo de cerdo, Huevo, Cerdo, Cebollín, Alga nori', 'Japón', 'Media', '35 minutos',
 '1. Preparar caldo rico y sabroso. 2. Cocer los fideos. 3. Cocinar la proteína. 4. Montar en bowl. 5. Agregar toppings.',
 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800', 190, 3),

('Churrasco Brasileño', 'Brasileña', 'Carne de res, Sal gruesa, Ajo, Chimichurri', 'Brasil', 'Fácil', '30 minutos',
 '1. Salar la carne generosamente. 2. Asar a fuego alto. 3. Cocer al punto deseado. 4. Reposar antes de cortar. 5. Servir con chimichurri.',
 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800', 140, 1);