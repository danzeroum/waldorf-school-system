#!/bin/bash
set -e

COMPOSE_FILE="docker-compose.homologacao.yml"
DB_CONTAINER="waldorf-homolog-db"
DB_ROOT_PASS="homolog_root_pass"
DB_NAME="waldorf_homolog"
BACKEND_IMAGE="ghcr.io/danzeroum/waldorf-school-system/backend:main"
FRONTEND_IMAGE="ghcr.io/danzeroum/waldorf-school-system/frontend:main"

echo ""
echo "=========================================="
echo "  WALDORF — Deploy Homologação"
echo "  $(date '+%d/%m/%Y %H:%M:%S')"
echo "=========================================="
echo ""

echo "[1/5] Pull das imagens Docker..."
docker pull ${BACKEND_IMAGE}
docker pull ${FRONTEND_IMAGE}
echo "      ✅ Imagens atualizadas"
echo ""

echo "[2/5] Parando containers..."
docker compose -f ${COMPOSE_FILE} down 2>/dev/null || docker-compose -f ${COMPOSE_FILE} down 2>/dev/null
echo "      ✅ Containers parados"
echo ""

echo "[3/5] Limpando banco de dados..."
if docker ps -a --format '{{.Names}}' | grep -q "${DB_CONTAINER}"; then
    docker exec ${DB_CONTAINER} mysql -u root -p${DB_ROOT_PASS} \
        -e "DROP DATABASE IF EXISTS ${DB_NAME}; CREATE DATABASE ${DB_NAME} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null \
        && echo "      ✅ Database recriado" || echo "      ⚠️  Limpando via container rm"
    docker rm -f ${DB_CONTAINER} 2>/dev/null || true
else
    echo "      Container DB não existe — clean start"
fi
echo ""

echo "[4/5] Subindo containers..."
docker compose -f ${COMPOSE_FILE} up -d 2>/dev/null || docker-compose -f ${COMPOSE_FILE} up -d 2>/dev/null
echo "      ✅ Containers subindo"
echo ""

echo "[5/5] Aguardando serviços..."
echo "      MySQL..."
for i in $(seq 1 30); do
    if docker exec ${DB_CONTAINER} mysqladmin ping -u root -p${DB_ROOT_PASS} --silent 2>/dev/null; then
        echo "      ✅ MySQL OK"
        break
    fi
    [ $i -eq 30 ] && echo "      ❌ MySQL não respondeu" && exit 1
    sleep 2
done

echo "      Backend..."
for i in $(seq 1 30); do
    if curl -sf http://localhost:8081/actuator/health 2>/dev/null >/dev/null; then
        echo "      ✅ Backend OK"
        break
    fi
    [ $i -eq 30 ] && echo "      ⚠️  Backend ainda iniciando"
    sleep 2
done

echo ""
echo "=========================================="
echo "  ✅ DEPLOY CONCLUÍDO"
echo "=========================================="
echo "  Backend:  http://localhost:8081"
echo "  Frontend: http://localhost:4201"
echo "  Nginx:    http://localhost:8090"
echo "  Admin:    admin@waldorf.edu.br / admin123"
echo "  Logs:     docker logs waldorf-homolog-backend -f"
echo "=========================================="
