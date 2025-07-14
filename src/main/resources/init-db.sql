-- Criação do banco de dados (caso não exista)
CREATE
DATABASE IF NOT EXISTS pessoa_db;

-- Usar o banco de dados
\c
pessoa_db;

-- Criação da tabela pessoa (será criada automaticamente pelo Hibernate, mas deixamos como referência)
-- CREATE TABLE IF NOT EXISTS pessoa (
--     id BIGSERIAL PRIMARY KEY,
--     nome VARCHAR(100) NOT NULL,
--     data_nascimento DATE NOT NULL,
--     CONSTRAINT uk_pessoa_nome_data UNIQUE (nome, data_nascimento)
-- );

-- Inserção de dados de exemplo
INSERT INTO pessoa (nome, data_nascimento)
VALUES ('João Silva', '1990-05-15'),
       ('Maria Santos', '1985-12-03'),
       ('Pedro Oliveira', '1992-08-20'),
       ('Ana Costa', '1988-03-10') ON CONFLICT DO NOTHING;