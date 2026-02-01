-- ================================================
-- WALDORF SCHOOL SYSTEM - MIGRATION V5
-- DADOS INICIAIS DO SISTEMA
-- ================================================

-- PERFIS PADRÃO
INSERT INTO perfis (nome, descricao, nivel_acesso, ativo) VALUES
('ADMIN', 'Administrador do sistema com acesso total', 10, TRUE),
('DIRETOR', 'Direção da escola', 9, TRUE),
('SECRETARIA', 'Secretária escolar', 7, TRUE),
('PROFESSOR', 'Professor', 5, TRUE),
('PROFESSOR_AUXILIAR', 'Professor auxiliar', 4, TRUE),
('RESPONSAVEL', 'Pais/Responsáveis de alunos', 3, TRUE),
('ALUNO', 'Estudante (para portal do aluno futuro)', 1, TRUE);

-- PERMISSÕES BASE
INSERT INTO permissoes (nome, descricao, recurso, acao, escopo) VALUES
-- Pessoas
('pessoas.read', 'Visualizar pessoas', 'PESSOAS', 'READ', 'GLOBAL'),
('pessoas.create', 'Criar pessoas', 'PESSOAS', 'CREATE', 'GLOBAL'),
('pessoas.update', 'Atualizar pessoas', 'PESSOAS', 'UPDATE', 'GLOBAL'),
('pessoas.delete', 'Deletar pessoas', 'PESSOAS', 'DELETE', 'GLOBAL'),

-- Alunos
('alunos.read.all', 'Visualizar todos os alunos', 'ALUNOS', 'READ', 'GLOBAL'),
('alunos.read.turma', 'Visualizar alunos da própria turma', 'ALUNOS', 'READ', 'TURMA'),
('alunos.read.filho', 'Visualizar dados dos próprios filhos', 'ALUNOS', 'READ', 'PROPRIO'),
('alunos.create', 'Criar alunos', 'ALUNOS', 'CREATE', 'GLOBAL'),
('alunos.update', 'Atualizar alunos', 'ALUNOS', 'UPDATE', 'GLOBAL'),
('alunos.delete', 'Deletar alunos', 'ALUNOS', 'DELETE', 'GLOBAL'),

-- Professores
('professores.read', 'Visualizar professores', 'PROFESSORES', 'READ', 'GLOBAL'),
('professores.create', 'Criar professores', 'PROFESSORES', 'CREATE', 'GLOBAL'),
('professores.update', 'Atualizar professores', 'PROFESSORES', 'UPDATE', 'GLOBAL'),

-- Pedagogia
('pedagogia.observacoes.read.all', 'Ver todas observações', 'OBSERVACOES', 'READ', 'GLOBAL'),
('pedagogia.observacoes.read.turma', 'Ver observações da turma', 'OBSERVACOES', 'READ', 'TURMA'),
('pedagogia.observacoes.read.filho', 'Ver observações dos filhos', 'OBSERVACOES', 'READ', 'PROPRIO'),
('pedagogia.observacoes.create', 'Criar observações', 'OBSERVACOES', 'CREATE', 'TURMA'),
('pedagogia.observacoes.update', 'Atualizar observações', 'OBSERVACOES', 'UPDATE', 'TURMA'),
('pedagogia.observacoes.delete', 'Deletar observações', 'OBSERVACOES', 'DELETE', 'TURMA'),

-- Épocas
('pedagogia.epocas.read', 'Visualizar épocas', 'EPOCAS', 'READ', 'TURMA'),
('pedagogia.epocas.create', 'Criar épocas', 'EPOCAS', 'CREATE', 'TURMA'),
('pedagogia.epocas.update', 'Atualizar épocas', 'EPOCAS', 'UPDATE', 'TURMA'),

-- Relatórios
('pedagogia.relatorios.read.all', 'Ver todos relatórios', 'RELATORIOS', 'READ', 'GLOBAL'),
('pedagogia.relatorios.read.turma', 'Ver relatórios da turma', 'RELATORIOS', 'READ', 'TURMA'),
('pedagogia.relatorios.read.filho', 'Ver relatórios dos filhos', 'RELATORIOS', 'READ', 'PROPRIO'),
('pedagogia.relatorios.create', 'Criar relatórios', 'RELATORIOS', 'CREATE', 'TURMA'),
('pedagogia.relatorios.approve', 'Aprovar relatórios', 'RELATORIOS', 'APPROVE', 'GLOBAL'),

-- Turmas
('turmas.read', 'Visualizar turmas', 'TURMAS', 'READ', 'GLOBAL'),
('turmas.create', 'Criar turmas', 'TURMAS', 'CREATE', 'GLOBAL'),
('turmas.update', 'Atualizar turmas', 'TURMAS', 'UPDATE', 'GLOBAL'),

-- Matrículas
('matriculas.read', 'Visualizar matrículas', 'MATRICULAS', 'READ', 'GLOBAL'),
('matriculas.create', 'Criar matrículas', 'MATRICULAS', 'CREATE', 'GLOBAL'),
('matriculas.update', 'Atualizar matrículas', 'MATRICULAS', 'UPDATE', 'GLOBAL');

-- PERFIS-PERMISSÕES (ADMIN tem tudo)
INSERT INTO perfis_permissoes (perfil_id, permissao_id)
SELECT 1, id FROM permissoes; -- Admin tem todas

-- PROFESSOR: apenas sua turma
INSERT INTO perfis_permissoes (perfil_id, permissao_id)
SELECT 4, id FROM permissoes WHERE escopo IN ('TURMA', 'PROPRIO') OR nome LIKE '%read%';

-- RESPONSÁVEL: apenas seus filhos
INSERT INTO perfis_permissoes (perfil_id, permissao_id)
SELECT 6, id FROM permissoes WHERE nome LIKE '%.read.filho';

-- SECRETÁRIA: gestão administrativa
INSERT INTO perfis_permissoes (perfil_id, permissao_id)
SELECT 3, id FROM permissoes WHERE recurso IN ('PESSOAS', 'ALUNOS', 'PROFESSORES', 'TURMAS', 'MATRICULAS');

-- CURSOS PADRÃO WALDORF
INSERT INTO cursos (nome, nivel_ensino, serie_inicial, serie_final, idade_recomendada_inicial, idade_recomendada_final, descricao, ordem_exibicao, cor_identificacao) VALUES
('Jardim de Infância', 'JARDIM', 0, 0, 3, 4, 'Jardim de Infância - Primeiro Setênio', 1, '#81C784'),
('Maternal', 'INFANTIL', 0, 0, 4, 6, 'Maternal - Educação Infantil', 2, '#64B5F6'),
('1° Setênio', 'FUNDAMENTAL_I', 1, 5, 7, 11, 'Ensino Fundamental I - Classes 1 a 5', 3, '#4CAF50'),
('2° Setênio', 'FUNDAMENTAL_II', 6, 9, 12, 14, 'Ensino Fundamental II - Classes 6 a 9', 4, '#2196F3'),
('3° Setênio', 'ENSINO_MEDIO', 10, 12, 15, 17, 'Ensino Médio - Classes 10 a 12', 5, '#FF9800');

-- DISCIPLINAS BÁSICAS
INSERT INTO disciplinas (codigo, nome, area_conhecimento, carga_horaria_total, ativo) VALUES
('PORT', 'Português', 'LINGUAGENS', 160, TRUE),
('MAT', 'Matemática', 'MATEMATICA', 160, TRUE),
('HIST', 'História', 'CIENCIAS_HUMANAS', 80, TRUE),
('GEO', 'Geografia', 'CIENCIAS_HUMANAS', 80, TRUE),
('CIEN', 'Ciências', 'CIENCIAS_NATUREZA', 80, TRUE),
('ARTE', 'Artes', 'ARTES', 80, TRUE),
('MUS', 'Música', 'ARTES', 80, TRUE),
('TMAN', 'Trabalhos Manuais', 'TRABALHOS_MANUAIS', 80, TRUE),
('EURITMIA', 'Euritmia', 'ARTES', 80, TRUE);

-- USUÁRIO ADMIN PADRÃO
-- Senha: Admin@2024 (deve ser trocada no primeiro login)
INSERT INTO pessoas (tipo, nome_completo, email, cpf, ativo, lgpd_consentimento_geral, lgpd_base_legal) VALUES
('OUTRO', 'Administrador do Sistema', 'admin@escolawaldorf.edu.br', '00000000000', TRUE, TRUE, 'LEGITIMO_INTERESSE');

INSERT INTO usuarios (pessoa_id, username, password_hash, email, ativo) VALUES
(1, 'admin', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'admin@escolawaldorf.edu.br', TRUE);

INSERT INTO usuarios_perfis (usuario_id, perfil_id, principal) VALUES
(1, 1, TRUE); -- Admin principal