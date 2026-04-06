#!/bin/bash
set -euo pipefail
BASE_URL="http://localhost:8090"
PASS=0; FAIL=0; ERROS=""
pass() { ((PASS++)); echo "  ✅ $1"; }
fail() { ((FAIL++)); ERROS+="\n  ❌ $1"; echo "  ❌ $1"; }
header() { echo ""; echo "━━━ $1 ━━━"; }

header "1. AUTH — Login"
LOGIN=$(curl -sf "$BASE_URL/api/v1/auth/login" -H "Content-Type: application/json" -d '{"email":"admin@waldorf.edu.br","password":"admin123"}') || { fail "Login falhou"; exit 1; }
TOKEN=$(echo "$LOGIN" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
[ -n "$TOKEN" ] && pass "Login OK — token obtido (${#TOKEN} chars)" || { fail "Token vazio"; exit 1; }
echo "$LOGIN" | grep -q "ADMIN" && pass "Perfil ADMIN confirmado" || fail "Perfil ADMIN não encontrado"

header "2. PROFESSORES — CRUD"
TS=$(date +%s)
PROF_NOME="Professor Teste $TS"
PROF_EMAIL="teste.$TS@waldorf.edu.br"
PROFESSOR=$(curl -sf -X POST "$BASE_URL/api/v1/professores" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d "{\"nome\":\"$PROF_NOME\",\"email\":\"$PROF_EMAIL\",\"especialidade\":\"Testing\"}") || { fail "POST /professores falhou"; }
PROF_ID=$(echo "$PROFESSOR" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
[ -n "$PROF_ID" ] && pass "Professor criado — ID: $PROF_ID" || fail "Professor sem ID"
echo "$PROFESSOR" | grep -q '"ativo":true' && pass "Professor ativo" || fail "Professor não ativo"

LISTA=$(curl -sf "$BASE_URL/api/v1/professores" -H "Authorization: Bearer $TOKEN") || { fail "GET /professores falhou"; }
echo "$LISTA" | grep -q "$PROF_EMAIL" && pass "Professor na listagem" || fail "Professor não listado"

PROF_GET=$(curl -sf "$BASE_URL/api/v1/professores/$PROF_ID" -H "Authorization: Bearer $TOKEN")
echo "$PROF_GET" | grep -q "$PROF_NOME" && pass "Busca por ID OK" || fail "Busca por ID falhou"

PROF_UPD=$(curl -sf -X PUT "$BASE_URL/api/v1/professores/$PROF_ID" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d "{\"nome\":\"$PROF_NOME (Atualizado)\",\"email\":\"$PROF_EMAIL\",\"especialidade\":\"DevOps\"}")
echo "$PROF_UPD" | grep -q "Atualizado" && pass "Professor atualizado" || fail "Atualização falhou"

curl -sf -X DELETE "$BASE_URL/api/v1/professores/$PROF_ID" -H "Authorization: Bearer $TOKEN" > /dev/null && pass "Professor inativado" || fail "DELETE falhou"
PROF_DEL=$(curl -sf "$BASE_URL/api/v1/professores/$PROF_ID" -H "Authorization: Bearer $TOKEN")
echo "$PROF_DEL" | grep -q '"ativo":false' && pass "Confirmado inativo no banco" || fail "Inativação não refletida"

header "3. TURMAS — CRUD"
TURMA_NOME="Turma Integração $TS"
TURMA=$(curl -sf -X POST "$BASE_URL/api/v1/turmas" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d "{\"nome\":\"$TURMA_NOME\",\"anoLetivo\":2026,\"anoEscolar\":3,\"capacidadeMaxima\":30}") || { fail "POST /turmas falhou"; }
TURMA_ID=$(echo "$TURMA" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
[ -n "$TURMA_ID" ] && pass "Turma criada — ID: $TURMA_ID" || fail "Turma sem ID"
TURMAS=$(curl -sf "$BASE_URL/api/v1/turmas" -H "Authorization: Bearer $TOKEN")
echo "$TURMAS" | grep -q "$TURMA_NOME" && pass "Turma na listagem" || fail "Turma não listada"

header "4. BANCO DE DADOS — Verificação Direta"
DB_PROF=$(docker exec waldorf-homolog-db mysql -u waldorf -pwaldorf_homolog_pass waldorf_homolog -se "SELECT COUNT(*) FROM professores WHERE email='$PROF_EMAIL';" 2>/dev/null)
[ "$DB_PROF" -eq 1 ] 2>/dev/null && pass "Professor '$PROF_EMAIL' no MySQL" || fail "Professor não no banco"
DB_TURMA=$(docker exec waldorf-homolog-db mysql -u waldorf -pwaldorf_homolog_pass waldorf_homolog -se "SELECT COUNT(*) FROM turmas WHERE nome='$TURMA_NOME';" 2>/dev/null)
[ "$DB_TURMA" -eq 1 ] 2>/dev/null && pass "Turma '$TURMA_NOME' no MySQL" || fail "Turma não no banco"
for TABELA in professores turmas alunos usuarios perfis; do
  DB_CHECK=$(docker exec waldorf-homolog-db mysql -u waldorf -pwaldorf_homolog_pass waldorf_homolog -se "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='waldorf_homolog' AND table_name='$TABELA';" 2>/dev/null)
  [ "$DB_CHECK" -eq 1 ] 2>/dev/null && pass "Tabela '$TABELA' existe" || fail "Tabela '$TABELA' ausente"
done

header "5. ALUNOS — Criar e Verificar"
ALUNO_NOME="Aluno Teste $TS"
ALUNO_MATRICULA="MAT$(date +%s)A"
ALUNO=$(curl -sf -X POST "$BASE_URL/api/v1/alunos" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d "{\"nome\":\"$ALUNO_NOME\",\"matricula\":\"$ALUNO_MATRICULA\",\"anoIngresso\":2026}") || { fail "POST /alunos falhou"; }
ALUNO_ID=$(echo "$ALUNO" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
[ -n "$ALUNO_ID" ] && pass "Aluno criado — matrícula: $ALUNO_MATRICULA" || fail "Aluno sem ID"
DB_ALUNO=$(docker exec waldorf-homolog-db mysql -u waldorf -pwaldorf_homolog_pass waldorf_homolog -se "SELECT COUNT(*) FROM alunos WHERE matricula='$ALUNO_MATRICULA';" 2>/dev/null)
[ "$DB_ALUNO" -eq 1 ] 2>/dev/null && pass "Aluno confirmado no banco" || fail "Aluno não no banco"

header "6. RESPONSÁVEIS — Criar e Verificar"
RESP_NOME="Responsável Teste $TS"
RESP=$(curl -sf -X POST "$BASE_URL/api/v1/responsaveis" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d "{\"nome\":\"$RESP_NOME\",\"email\":\"resp.$TS@teste.com\",\"telefone\":\"11999999999\"}") || { fail "POST /responsaveis falhou"; }
RESP_ID=$(echo "$RESP" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
[ -n "$RESP_ID" ] && pass "Responsável criado — ID: $RESP_ID" || fail "Responsável sem ID"
DB_RESP=$(docker exec waldorf-homolog-db mysql -u waldorf -pwaldorf_homolog_pass waldorf_homolog -se "SELECT COUNT(*) FROM responsaveis WHERE nome='$RESP_NOME';" 2>/dev/null)
[ "$DB_RESP" -eq 1 ] 2>/dev/null && pass "Responsável no banco" || fail "Responsável não no banco"

header "7. FRONTEND — SPA Routing"
for ROTA in "/" "/auth/login" "/dashboard" "/pedagogia/turmas" "/pessoas/professores" "/financeiro" "/comunidade"; do
  CODE=$(curl -sf -o /dev/null -w "%{http_code}" "$BASE_URL$ROTA")
  [ "$CODE" = "200" ] && pass "GET $ROTA → 200" || fail "GET $ROTA → $CODE"
done

header "8. SEGURANÇA"
CODE_SEM=$(curl -sf -o /dev/null -w "%{http_code}" "$BASE_URL/api/v1/professores")
[ "$CODE_SEM" = "403" ] && pass "Sem token → 403" || fail "Sem token → $CODE_SEM"
CODE_INV=$(curl -sf -o /dev/null -w "%{http_code}" "$BASE_URL/api/v1/professores" -H "Authorization: Bearer invalido")
[ "$CODE_INV" = "403" ] && pass "Token inválido → 403" || fail "Token inválido → $CODE_INV"

echo ""
echo "═══════════════════════════════════════════════"
echo "  📊 RESULTADO: ✅ $PASS passou | ❌ $FAIL falhou"
echo "═══════════════════════════════════════════════"
[ $FAIL -eq 0 ] && echo -e "\n🎉 TODOS OS TESTES PASSARAM!\n" && exit 0
echo -e "\nErros:$ERROS\n"; exit 1
