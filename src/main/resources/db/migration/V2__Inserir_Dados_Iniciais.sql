-- V2__Inserir_Dados_Iniciais.sql

-- Inserir papéis se eles não foram inseridos anteriormente (garantir idempotência)
INSERT INTO papeis (nome) VALUES ('ROLE_ADMIN') ON CONFLICT (nome) DO NOTHING;
INSERT INTO papeis (nome) VALUES ('ROLE_CLIENTE') ON CONFLICT (nome) DO NOTHING;
INSERT INTO papeis (nome) VALUES ('ROLE_DENTISTA') ON CONFLICT (nome) DO NOTHING;

-- Senha para 'admin' (admin): $2a$10$T8k.4XwWq.zJ8y.9xX.8.O.p.2aJ.2y.2N.2o.2S.2d.2e.2r.2f.2g.2h.2i.2j.2k.2l.2m.2n.2o.2p.2q.2r.2s.2t.2u.2v.2w.2x.2y.2z
INSERT INTO usuarios (login, email, senha, data_criacao, ativo)
VALUES ('admin', 'admin@odontocare.com', '$2a$10$/U9RY4g3DFy6S7quOkE6VulMH03Z5O/u.5tM77j1FfBYtp0Sral3y', NOW(), TRUE)
ON CONFLICT (login) DO NOTHING;

-- Associar o papel ROLE_ADMIN ao usuário 'admin'
INSERT INTO usuario_papel (usuario_id, papel_id)
SELECT u.id, p.id FROM usuarios u, papeis p
WHERE u.login = 'admin' AND p.nome = 'ROLE_ADMIN'
ON CONFLICT (usuario_id, papel_id) DO NOTHING;

------------------------------------------------------------------------------------------------------------------------
-- DADOS DE TESTE INICIAIS
------------------------------------------------------------------------------------------------------------------------

-- Senha comum para testes (para 'senha123'): 

-- CLIENTES DE TESTE (10 Clientes)
INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('joao.silva', 'joao.silva@email.com', '$2a$10$wl6qYUX77DvzDYrljPseB.Zaa3iGFyQ2wNhf6jqlKJz2efqgCc9gm', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO clientes (id, nome_cliente, endereco, telefone, idade)
SELECT u.id, 'João Silva', 'Rua A, 123', '34991234567', 30 FROM usuarios u WHERE u.login = 'joao.silva' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'joao.silva' AND p.nome = 'ROLE_CLIENTE' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('maria.santos', 'maria.santos@email.com', '$2a$10$FyXABhvZqrxz79an4gT/J.CH8OZqQ0wBx/.F5KLtsrf6Lm0OEzALK', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO clientes (id, nome_cliente, endereco, telefone, idade)
SELECT u.id, 'Maria Santos', 'Avenida B, 456', '34992345678', 25 FROM usuarios u WHERE u.login = 'maria.santos' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'maria.santos' AND p.nome = 'ROLE_CLIENTE' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('pedro.almeida', 'pedro.almeida@email.com', '$2a$10$gZnXWU99QBwUOc7oCXNHHet9s3hoxwmQHaO.QL3aj89FqrUM1YSee', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO clientes (id, nome_cliente, endereco, telefone, idade)
SELECT u.id, 'Pedro Almeida', 'Rua C, 789', '34993456789', 40 FROM usuarios u WHERE u.login = 'pedro.almeida' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'pedro.almeida' AND p.nome = 'ROLE_CLIENTE' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('ana.costa', 'ana.costa@email.com', '$2a$10$/vVNkfPY/lQQPCAzS/aiPenDmXtCjyiR77KAsPpLOk7ftKaoNo9.u', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO clientes (id, nome_cliente, endereco, telefone, idade)
SELECT u.id, 'Ana Costa', 'Avenida D, 101', '34994567890', 35 FROM usuarios u WHERE u.login = 'ana.costa' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'ana.costa' AND p.nome = 'ROLE_CLIENTE' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('luiz.fernandes', 'luiz.fernandes@email.com', '$2a$10$wJyKvxK7KlUnZzsIt59ynuLe78bLQN7VaVWOgMmh4kYFlSTMEfyTW', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO clientes (id, nome_cliente, endereco, telefone, idade)
SELECT u.id, 'Luiz Fernandes', 'Rua E, 222', '34995678901', 50 FROM usuarios u WHERE u.login = 'luiz.fernandes' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'luiz.fernandes' AND p.nome = 'ROLE_CLIENTE' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('sofia.rodrigues', 'sofia.rodrigues@email.com', '$2a$10$QkhtH8npVT98ToPxW9iR9eHlHGi9HyVR8dCfKDVm72NS58q4sc7lW', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO clientes (id, nome_cliente, endereco, telefone, idade)
SELECT u.id, 'Sofia Rodrigues', 'Avenida F, 333', '34996789012', 28 FROM usuarios u WHERE u.login = 'sofia.rodrigues' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'sofia.rodrigues' AND p.nome = 'ROLE_CLIENTE' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('carlos.gomes', 'carlos.gomes@email.com', '$2a$10$C7IxvFi8YpDEDjeXdeSgj.PmgjpLREpsQx61i3lennQLVcBK1Eame', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO clientes (id, nome_cliente, endereco, telefone, idade)
SELECT u.id, 'Carlos Gomes', 'Rua G, 444', '34997890123', 45 FROM usuarios u WHERE u.login = 'carlos.gomes' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'carlos.gomes' AND p.nome = 'ROLE_CLIENTE' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('julia.lima', 'julia.lima@email.com', '$2a$10$xaZyoDMRcIkYAF6F3hokVepl5030Nx9Nkxe0TTpLI8ICte.2nrAPe', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO clientes (id, nome_cliente, endereco, telefone, idade)
SELECT u.id, 'Julia Lima', 'Avenida H, 555', '34998901234', 33 FROM usuarios u WHERE u.login = 'julia.lima' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'julia.lima' AND p.nome = 'ROLE_CLIENTE' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('rafael.pereira', 'rafael.pereira@email.com', '$2a$10$5lqjFVc7VQMw0lh8csFEq.AiBOFiExaomPZXbvDdwqinwEOnzCCVS', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO clientes (id, nome_cliente, endereco, telefone, idade)
SELECT u.id, 'Rafael Pereira', 'Rua I, 666', '34999012345', 22 FROM usuarios u WHERE u.login = 'rafael.pereira' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'rafael.pereira' AND p.nome = 'ROLE_CLIENTE' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('beatriz.silva', 'beatriz.silva@email.com', '$2a$10$6DPzPQdkvRzQTGa5bEwJVepu8CP.GB8u6jzigjXHH4oB5e8oGVaaO', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO clientes (id, nome_cliente, endereco, telefone, idade)
SELECT u.id, 'Beatriz Silva', 'Avenida J, 777', '34990123456', 38 FROM usuarios u WHERE u.login = 'beatriz.silva' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'beatriz.silva' AND p.nome = 'ROLE_CLIENTE' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

-- DENTISTAS DE TESTE (3 Dentistas)
INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('dr.roberto', 'roberto.s@odontocare.com', '$2a$10$hJkAkKWlN62b5y5flyCs2eYXbZyKghQqe.bSw9ES/scwwS.rrkxgK', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO dentistas (id, nome_adm, cro)
SELECT u.id, 'Dr. Roberto Santos', 'CRO/MG-12345' FROM usuarios u WHERE u.login = 'dr.roberto' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'dr.roberto' AND p.nome = 'ROLE_DENTISTA' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('dra.ana', 'ana.m@odontocare.com', '$2a$10$9yrWn.Zk0JpQl0Fb1Enc9uH8m0aN5bV59hxKlKhDI9K1tXDjGml5m', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO dentistas (id, nome_adm, cro)
SELECT u.id, 'Dra. Ana Maria', 'CRO/SP-67890' FROM usuarios u WHERE u.login = 'dra.ana' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'dra.ana' AND p.nome = 'ROLE_DENTISTA' ON CONFLICT (usuario_id, papel_id) DO NOTHING;

INSERT INTO usuarios (login, email, senha, data_criacao, ativo) VALUES
('dr.felipe', 'felipe.c@odontocare.com', '$2a$10$b7atL1S6YJWfMj98TTHsruQwFyq7oIQ4CkqwPCqQt5wBUO/GYrnby', NOW(), TRUE) ON CONFLICT (login) DO NOTHING;
INSERT INTO dentistas (id, nome_adm, cro)
SELECT u.id, 'Dr. Felipe Costa', 'CRO/RJ-11223' FROM usuarios u WHERE u.login = 'dr.felipe' ON CONFLICT (id) DO NOTHING;
INSERT INTO usuario_papel (usuario_id, papel_id) SELECT u.id, p.id FROM usuarios u, papeis p WHERE u.login = 'dr.felipe' AND p.nome = 'ROLE_DENTISTA' ON CONFLICT (usuario_id, papel_id) DO NOTHING;