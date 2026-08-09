CREATE TABLE tb_document (
                             id UUID PRIMARY KEY,
                             type VARCHAR(20) NOT NULL,
                             value VARCHAR(50) NOT NULL,
                             customer_id UUID NOT NULL,
                             CONSTRAINT fk_document_customer FOREIGN KEY (customer_id) REFERENCES tb_customer(id) ON DELETE CASCADE
);