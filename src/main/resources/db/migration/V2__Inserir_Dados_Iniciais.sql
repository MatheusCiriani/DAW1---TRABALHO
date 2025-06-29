-- V2__Inserir_Dados_Iniciais.sql

-- Inserir papéis se eles não foram inseridos anteriormente (garantir idempotência)
INSERT INTO papeis (nome) VALUES ('ROLE_ADMIN') ON CONFLICT (nome) DO NOTHING;
INSERT INTO papeis (nome) VALUES ('ROLE_CLIENTE') ON CONFLICT (nome) DO NOTHING;
INSERT INTO papeis (nome) VALUES ('ROLE_DENTISTA') ON CONFLICT (nome) DO NOTHING;

-- Inserir um usuário administrador
-- Senha 'admin' criptografada com BCrypt: $2a$10$T8k.4XwWq.zJ8y.9xX.8.O.p.2aJ.2y.2N.2o.2S.2d.2e.2r.2f.2g.2h.2i.2j.2k.2l.2m.2n.2o.2p.2q.2r.2s.2t.2u.2v.2w.2x.2y.2z
INSERT INTO usuarios (login, email, senha, data_criacao, ativo)
VALUES ('admin', 'admin@odontocare.com', '$2a$10$/U9RY4g3DFy6S7quOkE6VulMH03Z5O/u.5tM77j1FfBYtp0Sral3y', NOW(), TRUE)
ON CONFLICT (login) DO NOTHING;

-- Associar o papel ROLE_ADMIN ao usuário 'admin'
INSERT INTO usuario_papel (usuario_id, papel_id)
SELECT u.id, p.id FROM usuarios u, papeis p
WHERE u.login = 'admin' AND p.nome = 'ROLE_ADMIN'
ON CONFLICT (usuario_id, papel_id) DO NOTHING;