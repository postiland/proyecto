-- TABLA USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
  id_usuario SMALLINT NOT NULL AUTO_INCREMENT,
  email varchar(80) DEFAULT NULL,
  nombre varchar(30) NOT NULL,
  apellidos varchar(100) NOT NULL,
  telefono int(9) DEFAULT NULL,
  contrasena varchar(30) NOT NULL,
  PRIMARY KEY (id_usuario)
);

-- TABLA LISTAS
CREATE TABLE IF NOT EXISTS listas (
  id_lista SMALLINT NOT NULL AUTO_INCREMENT,
  nombre varchar(30) NOT NULL,
  codigo_grupo varchar(50) NOT NULL,
  id_usuario SMALLINT NOT NULL,
  PRIMARY KEY (id_lista),
  FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario) ON DELETE CASCADE
);

-- TABLA USUARIOS_LISTAS
CREATE TABLE IF NOT EXISTS usuarios_listas (
  id_usuario SMALLINT NOT NULL AUTO_INCREMENT,
  id_lista SMALLINT NOT NULL,
  PRIMARY KEY (id_usuario,id_lista),
  FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario) ON DELETE CASCADE,
  FOREIGN KEY (id_lista) REFERENCES listas (id_lista) ON DELETE CASCADE
);

-- TABLA PRODUCTOS
CREATE TABLE IF NOT EXISTS productos (
  id_producto SMALLINT NOT NULL AUTO_INCREMENT,
  nombre varchar(30) NOT NULL,
  precio float NOT NULL,
  PRIMARY KEY (id_producto)
);

-- TABLA LISTAS_PRODUCTOS
CREATE TABLE IF NOT EXISTS listas_productos (
  id_producto SMALLINT NOT NULL AUTO_INCREMENT,
  id_lista SMALLINT NOT NULL,
  cantidad SMALLINT NOT NULL,
  PRIMARY KEY (id_producto,id_lista),
  FOREIGN KEY (id_producto) REFERENCES productos (id_producto) ON DELETE CASCADE,
  FOREIGN KEY (id_lista) REFERENCES listas (id_lista) ON DELETE CASCADE
);

-- DATOS USUARIOS
INSERT INTO `usuarios`(`nombre`, `apellidos`, `email`, `telefono`, `contrasena`) VALUES ('Antonio','Postigo Martin','postiland@gmail.com',697293282,'123456');
INSERT INTO `usuarios`(`nombre`, `apellidos`, `email`, `telefono`, `contrasena`) VALUES ('Antonio','Ruiz','antonio@gmail.com',666666666,'123456');
INSERT INTO `usuarios`(`nombre`, `apellidos`, `email`, `telefono`, `contrasena`) VALUES ('Enrique','Alcaraz','enrique@gmail.com',666666666,'123456');
INSERT INTO `usuarios`(`nombre`, `apellidos`, `email`, `telefono`, `contrasena`) VALUES ('Samuel','Rabadan','samuel@gmail.com',666666666,'123456');

-- DATOS LISTAS
INSERT INTO `listas`(`nombre`, `codigo_grupo`, `id_usuario`) VALUES ('Mi lista','ABC123',1);
INSERT INTO `listas`(`nombre`, `codigo_grupo`, `id_usuario`) VALUES ('Lista de la casa','ABC123',4);

-- DATOS USUARIOS_LISTAS
INSERT INTO `usuarios_listas`(`id_usuario`, `id_lista`) VALUES (1,1);
INSERT INTO `usuarios_listas`(`id_usuario`, `id_lista`) VALUES (2,1);
INSERT INTO `usuarios_listas`(`id_usuario`, `id_lista`) VALUES (3,1);
INSERT INTO `usuarios_listas`(`id_usuario`, `id_lista`) VALUES (4,2);
INSERT INTO `usuarios_listas`(`id_usuario`, `id_lista`) VALUES (2,2);
INSERT INTO `usuarios_listas`(`id_usuario`, `id_lista`) VALUES (3,2);

-- DATOS PRODUCTOS
INSERT INTO `productos`(`nombre`, `precio`) VALUES ('Acuarius',1.4);
INSERT INTO `productos`(`nombre`, `precio`) VALUES ('Agua',0.35);
INSERT INTO `productos`(`nombre`, `precio`) VALUES ('Cinta de lomo',3.4);
INSERT INTO `productos`(`nombre`, `precio`) VALUES ('Detergente',5.4);
INSERT INTO `productos`(`nombre`, `precio`) VALUES ('Suavizante',3.4);
INSERT INTO `productos`(`nombre`, `precio`) VALUES ('Pan',0.50);
INSERT INTO `productos`(`nombre`, `precio`) VALUES ('Chorizo',1);
INSERT INTO `productos`(`nombre`, `precio`) VALUES ('Aceitunas',1.7);

-- DATOS PRODUCTOS_LISTAS
INSERT INTO `listas_productos`(`id_producto`, `id_lista`, `cantidad`) VALUES (1,1,3);
INSERT INTO `listas_productos`(`id_producto`, `id_lista`, `cantidad`) VALUES (2,1,3);
INSERT INTO `listas_productos`(`id_producto`, `id_lista`, `cantidad`) VALUES (3,1,3);
INSERT INTO `listas_productos`(`id_producto`, `id_lista`, `cantidad`) VALUES (1,2,3);
INSERT INTO `listas_productos`(`id_producto`, `id_lista`, `cantidad`) VALUES (8,1,3);
INSERT INTO `listas_productos`(`id_producto`, `id_lista`, `cantidad`) VALUES (7,2,3);
INSERT INTO `listas_productos`(`id_producto`, `id_lista`, `cantidad`) VALUES (2,2,3);
INSERT INTO `listas_productos`(`id_producto`, `id_lista`, `cantidad`) VALUES (5,2,3);
INSERT INTO `listas_productos`(`id_producto`, `id_lista`, `cantidad`) VALUES (4,2,3);
INSERT INTO `listas_productos`(`id_producto`, `id_lista`, `cantidad`) VALUES (3,2,3);