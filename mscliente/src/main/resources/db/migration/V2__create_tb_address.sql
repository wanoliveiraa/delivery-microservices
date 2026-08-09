CREATE TABLE tb_address (
                            id UUID PRIMARY KEY,
                            street VARCHAR(255) NOT NULL,
                            number VARCHAR(50) NOT NULL,
                            city VARCHAR(100) NOT NULL,
                            state VARCHAR(50) NOT NULL,
                            complement VARCHAR(100),
                            zip_code VARCHAR(20) NOT NULL,
                            customer_id UUID NOT NULL,
                            CONSTRAINT fk_address_customer FOREIGN KEY (customer_id) REFERENCES tb_customer(id)
);