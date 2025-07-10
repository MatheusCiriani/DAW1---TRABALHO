-- V4__Inserir_Consultas_Passadas.sql
-- Este script insere aproximadamente 40 consultas passadas para fins de teste.
-- As consultas são distribuídas entre os clientes e dentistas existentes.
-- As datas são retroativas a partir de 09/07/2025, pulando fins de semana.

-- Função para buscar o ID do usuário pelo login (para evitar hardcoding)
-- NOTA: Como não podemos criar funções em um script de migração simples,
-- vamos usar subqueries para buscar os IDs, mantendo o padrão do seu V2.

-- Consultas para Dra. Ana Maria (ID do usuário com login 'dra.ana')
INSERT INTO consultas (cliente_id, dentista_id, data_hora, horario, status) VALUES
((SELECT id FROM usuarios WHERE login = 'joao.silva'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-09 09:00:00', '09:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'maria.santos'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-09 10:00:00', '10:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'pedro.almeida'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-08 11:00:00', '11:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'ana.costa'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-08 14:00:00', '14:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'luiz.fernandes'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-07 09:00:00', '09:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'sofia.rodrigues'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-07 15:00:00', '15:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'carlos.gomes'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-04 10:00:00', '10:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'julia.lima'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-04 11:00:00', '11:00:00', 'CANCELADA'),
((SELECT id FROM usuarios WHERE login = 'rafael.pereira'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-03 16:00:00', '16:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'beatriz.silva'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-03 09:00:00', '09:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'joao.silva'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-02 13:00:00', '13:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'maria.santos'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-02 14:00:00', '14:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'pedro.almeida'), (SELECT id FROM usuarios WHERE login = 'dra.ana'), '2025-07-01 10:00:00', '10:00:00', 'REALIZADA');

-- Consultas para Dr. Roberto Santos (ID do usuário com login 'dr.roberto')
INSERT INTO consultas (cliente_id, dentista_id, data_hora, horario, status) VALUES
((SELECT id FROM usuarios WHERE login = 'ana.costa'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-09 11:00:00', '11:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'luiz.fernandes'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-09 15:00:00', '15:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'sofia.rodrigues'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-08 09:00:00', '09:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'carlos.gomes'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-08 10:00:00', '10:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'julia.lima'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-07 14:00:00', '14:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'rafael.pereira'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-07 16:00:00', '16:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'beatriz.silva'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-04 09:00:00', '09:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'joao.silva'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-03 11:00:00', '11:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'maria.santos'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-03 13:00:00', '13:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'pedro.almeida'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-02 15:00:00', '15:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'ana.costa'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-02 16:00:00', '16:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'luiz.fernandes'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-01 11:00:00', '11:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'sofia.rodrigues'), (SELECT id FROM usuarios WHERE login = 'dr.roberto'), '2025-07-01 14:00:00', '14:00:00', 'CANCELADA');

-- Consultas para Dr. Felipe Costa (ID do usuário com login 'dr.felipe')
INSERT INTO consultas (cliente_id, dentista_id, data_hora, horario, status) VALUES
((SELECT id FROM usuarios WHERE login = 'carlos.gomes'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-09 14:00:00', '14:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'julia.lima'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-09 16:00:00', '16:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'rafael.pereira'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-08 15:00:00', '15:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'beatriz.silva'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-08 16:00:00', '16:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'joao.silva'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-07 10:00:00', '10:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'maria.santos'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-07 11:00:00', '11:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'pedro.almeida'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-04 14:00:00', '14:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'ana.costa'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-04 15:00:00', '15:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'luiz.fernandes'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-03 10:00:00', '10:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'sofia.rodrigues'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-03 14:00:00', '14:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'carlos.gomes'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-02 09:00:00', '09:00:00', 'REALIZADA'),
((SELECT id FROM usuarios WHERE login = 'julia.lima'), (SELECT id FROM usuarios WHERE login = 'dr.felipe'), '2025-07-01 16:00:00', '16:00:00', 'REALIZADA');
