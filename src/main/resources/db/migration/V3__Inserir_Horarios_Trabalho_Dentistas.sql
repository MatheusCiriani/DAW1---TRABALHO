-- V3__Inserir_Horarios_Trabalho_Dentistas.sql

-- AGENDAS DE TESTE (Horários de trabalho fixos para os 3 dentistas - Seg a Sex, 09:00-17:00)

-- Horários para Dr. Roberto Santos
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'MONDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'roberto.s@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'TUESDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'roberto.s@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'WEDNESDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'roberto.s@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'THURSDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'roberto.s@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'FRIDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'roberto.s@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;

-- Horários para Dra. Ana Maria
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'MONDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'ana.m@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'TUESDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'ana.m@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'WEDNESDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'ana.m@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'THURSDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'ana.m@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'FRIDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'ana.m@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;

-- Horários para Dr. Felipe Costa
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'MONDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'felipe.c@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'TUESDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'felipe.c@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'WEDNESDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'felipe.c@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'THURSDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'felipe.c@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;
INSERT INTO agendas (dentista_id, dia_da_semana, hora_inicio, hora_fim)
SELECT d.id, 'FRIDAY', '09:00:00', '17:00:00' FROM dentistas d JOIN usuarios u ON d.id = u.id WHERE u.email = 'felipe.c@odontocare.com' ON CONFLICT (dentista_id, dia_da_semana) DO NOTHING;