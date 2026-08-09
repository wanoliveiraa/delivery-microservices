CREATE TABLE tb_customer (
                             id UUID PRIMARY KEY,
                             user_id UUID NOT NULL UNIQUE,
                             name VARCHAR(255) NOT NULL,
                             phone VARCHAR(50),
                             person_type VARCHAR(20) NOT NULL,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL,
                             deleted_at TIMESTAMP
)

