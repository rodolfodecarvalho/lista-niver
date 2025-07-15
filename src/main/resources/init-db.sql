-- Criação do banco de dados (caso não exista)
CREATE SCHEMA IF NOT EXISTS pessoa_db;


-- Definir o schema como padrão para este script
SET search_path TO pessoa_db, public;

-- Criação da tabela pessoa (será criada automaticamente pelo Hibernate, mas deixamos como referência)

-- Remove as tabelas se existirem (para recriar)
DROP TABLE IF EXISTS email;
DROP TABLE IF EXISTS pessoa;

-- Tabela pessoa
CREATE TABLE pessoa
(
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    data_nascimento DATE         NOT NULL
);

-- Tabela email
CREATE TABLE email
(
    id        BIGSERIAL PRIMARY KEY,
    email     VARCHAR(100) NOT NULL,
    pessoa_id BIGINT       NOT NULL,
    FOREIGN KEY (pessoa_id) REFERENCES pessoa (id) ON DELETE CASCADE
);

-- Índices para melhor performance
CREATE INDEX idx_pessoa_nome ON pessoa (nome);
CREATE INDEX idx_pessoa_data_nascimento ON pessoa (data_nascimento);
CREATE INDEX idx_email_pessoa_id ON email (pessoa_id);
CREATE INDEX idx_email_email ON email (email);

-- Dados de exemplo para teste
INSERT INTO pessoa (nome, data_nascimento)
VALUES ('João Silva', '1990-05-15'),
       ('Maria Santos', '1985-08-22'),
       ('Carlos Oliveira', '1992-12-10'),
       ('Ana Costa', '1988-03-07'),
       ('Pedro Almeida', '1995-11-30')
ON CONFLICT DO NOTHING;

INSERT INTO email (email, pessoa_id)
VALUES ('joao.silva@email.com', 1),
       ('joao.trabalho@empresa.com', 1),
       ('maria.santos@email.com', 2),
       ('carlos.oliveira@email.com', 3),
       ('carlos.pessoal@gmail.com', 3),
       ('carlos.profissional@empresa.com', 3),
       ('ana.costa@email.com', 4),
       ('pedro.almeida@email.com', 5),
       ('pedro.backup@email.com', 5)
ON CONFLICT DO NOTHING;