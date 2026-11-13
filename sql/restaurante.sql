-- =====================================================
-- Sistema Restaurante - Base de datos MySQL 8.x
-- =====================================================
CREATE DATABASE IF NOT EXISTS db_restaurante
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_restaurante;

DROP TABLE IF EXISTS plato;

CREATE TABLE plato (
    id_plato            INT            NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(100)   NOT NULL,
    categoria           VARCHAR(50)    NOT NULL,
    descripcion         VARCHAR(255)   NULL,
    precio              DECIMAL(8,2)   NOT NULL,
    tiempo_preparacion  INT            NOT NULL COMMENT 'Minutos',
    disponibilidad      TINYINT(1)     NOT NULL DEFAULT 1 COMMENT '1 = Disponible, 0 = Agotado',
    CONSTRAINT pk_plato PRIMARY KEY (id_plato),
    CONSTRAINT chk_plato_precio CHECK (precio >= 0),
    CONSTRAINT chk_plato_tiempo CHECK (tiempo_preparacion > 0)
) ENGINE = InnoDB;

-- Datos de prueba
INSERT INTO plato (nombre, categoria, descripcion, precio, tiempo_preparacion, disponibilidad) VALUES
('Ceviche clásico',        'Entrada',        'Pescado fresco marinado en limón con cebolla, ají limo y camote',          32.00, 15, 1),
('Causa limeña',           'Entrada',        'Causa de papa amarilla rellena de pollo y palta',                          18.00, 10, 1),
('Papa a la huancaína',    'Entrada',        'Papa sancochada con crema de ají amarillo y queso fresco',                 15.00,  8, 1),
('Lomo saltado',           'Plato de fondo', 'Lomo fino salteado con cebolla, tomate y papas fritas, acompañado de arroz', 34.00, 20, 1),
('Ají de gallina',         'Plato de fondo', 'Pollo deshilachado en crema de ají amarillo con arroz y papa',             28.00, 25, 1),
('Arroz con mariscos',     'Plato de fondo', 'Arroz al estilo peruano con mariscos frescos y ají panca',                 38.00, 30, 0),
('Tallarines verdes',      'Plato de fondo', 'Tallarines en salsa de albahaca y espinaca con bistec',                    29.00, 22, 1),
('Sopa criolla',           'Sopa',           'Sopa de carne con fideos, huevo y leche',                                  16.00, 18, 1),
('Aguadito de pollo',      'Sopa',           'Caldo de pollo con arroz, culantro y verduras',                            17.00, 25, 1),
('Suspiro a la limeña',    'Postre',         'Manjar blanco con merengue al oporto',                                     12.00,  5, 1),
('Chicha morada (jarra)',  'Bebida',         'Bebida de maíz morado con piña y canela',                                  14.00,  3, 1),
('Limonada frozen',        'Bebida',         'Limonada helada con hierbabuena',                                          10.00,  4, 0);

-- Verificación
DESCRIBE plato;
SELECT * FROM plato;
