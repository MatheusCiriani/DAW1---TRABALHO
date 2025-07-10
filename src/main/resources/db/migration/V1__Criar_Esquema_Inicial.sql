-- V1__Criar_Esquema_Inicial.sql

-- Adiciona a nova coluna 'ativo' à tabela 'usuarios' ANTES de criar a tabela
-- NOTA: A coluna 'ativo' será adicionada diretamente na definição da tabela 'usuarios'
--       e os valores padrão serão definidos lá, eliminando a necessidade do ALTER TABLE.
--       No entanto, como seu V1 original não tem 'ativo', e V4 só ALTERA,
--       vamos fazer um ALIGNMENT aqui para que a tabela 'usuarios' já nasça com 'ativo'.
--       Se o V1 fosse seu V1_original, e V4 fosse seu V4_original,
--       manteríamos V1 (criação) e V2 (alteração) separado.
--       MAS como queremos reduzir e garantir ordem, vamos INCLUIR ATIVO no CREATE TABLE.

-- Tabela USUARIOS
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE -- COLUNA 'ATIVO' INCLUÍDA DIRETAMENTE AQUI
);

-- Tabela CLIENTES (herda de USUARIOS)
CREATE TABLE clientes (
    id BIGINT PRIMARY KEY,
    nome_cliente VARCHAR(255) NOT NULL,
    endereco VARCHAR(500),
    telefone VARCHAR(20),
    idade INTEGER,
    FOREIGN KEY (id) REFERENCES usuarios (id)
);

-- Tabela DENTISTAS (herda de USUARIOS)
CREATE TABLE dentistas (
    id BIGINT PRIMARY KEY,
    nome_adm VARCHAR(255) NOT NULL,
    cro VARCHAR(20) UNIQUE NOT NULL,
    FOREIGN KEY (id) REFERENCES usuarios (id)
);
-- Tabela AGENDAS (Horarios de Trabalho)
-- Tabela AGENDAS (Horarios de Trabalho)
CREATE TABLE agendas (
    id BIGSERIAL PRIMARY KEY,
    dentista_id BIGINT NOT NULL,
    dia_da_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    FOREIGN KEY (dentista_id) REFERENCES dentistas (id),
    UNIQUE (dentista_id, dia_da_semana) -- NOVO: Restrição de unicidade
);
-- Tabela CONSULTAS
CREATE TABLE consultas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    dentista_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    horario TIME NOT NULL, -- NOVA COLUNA ADICIONADA
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES clientes (id),
    FOREIGN KEY (dentista_id) REFERENCES dentistas (id)
);

-- Tabela PAPEIS
CREATE TABLE papeis (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) UNIQUE NOT NULL -- Ex: 'ROLE_ADMIN', 'ROLE_USER', 'ROLE_DENTISTA', 'ROLE_CLIENTE'
);

-- Tabela de junção USUARIO_PAPEL para relacionamento ManyToMany
CREATE TABLE usuario_papel (
    usuario_id BIGINT NOT NULL,
    papel_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, papel_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    FOREIGN KEY (papel_id) REFERENCES papeis (id)
);

-- Nota: A tabela 'pagamentos' foi removida do escopo do projeto,
-- então não a incluímos aqui.