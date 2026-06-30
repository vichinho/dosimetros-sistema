-- HU #16: un cliente tiene un ejecutivo responsable, aunque todavía no tenga
-- dosímetros asignados. Permite que el ejecutivo vea sus clientes pendientes.

ALTER TABLE cliente
    ADD COLUMN ejecutivo_id INT NULL,
    ADD CONSTRAINT fk_cliente_ejecutivo
        FOREIGN KEY (ejecutivo_id) REFERENCES ejecutivo (id);

CREATE INDEX idx_cliente_ejecutivo ON cliente (ejecutivo_id);
