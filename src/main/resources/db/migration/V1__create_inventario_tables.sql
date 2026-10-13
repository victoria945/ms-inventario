CREATE TABLE IF NOT EXISTS inventario_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_pastel VARCHAR(30) NOT NULL,
    nombre_pastel VARCHAR(120) NOT NULL,
    stock INT NOT NULL,
    stock_inicial INT NOT NULL,
    compra_id BIGINT NULL,
    fecha_ingreso DATETIME NOT NULL,
    fecha_vencimiento DATE NULL
);

CREATE INDEX idx_inv_codigo ON inventario_item (codigo_pastel);

CREATE TABLE IF NOT EXISTS movimiento_inventario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventario_item_id BIGINT NULL,
    codigo_pastel VARCHAR(30) NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    motivo VARCHAR(255) NULL,
    usuario_rut VARCHAR(12) NOT NULL,
    fecha_movimiento DATETIME NOT NULL,
    CONSTRAINT fk_mov_inv_item FOREIGN KEY (inventario_item_id) REFERENCES inventario_item (id) ON DELETE SET NULL
);

CREATE INDEX idx_mov_codigo ON movimiento_inventario (codigo_pastel);
