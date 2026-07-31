#!/usr/bin/env bash
# Backup de la base de datos MySQL a un archivo .sql.gz.
# Uso:   ./backup-db.sh [carpeta_destino]   (por defecto: ./backups)
# Conserva los últimos 14 backups y borra los más antiguos.
set -euo pipefail

cd "$(dirname "$0")"

# Carga las variables (DB_PASSWORD, etc.) desde .env
if [ -f .env ]; then
  set -a; . ./.env; set +a
fi

DEST="${1:-./backups}"
mkdir -p "$DEST"
STAMP="$(date +%Y%m%d-%H%M%S)"
FILE="$DEST/dosimetros_db-$STAMP.sql.gz"

docker compose exec -T db \
  mysqldump -udosimetros -p"$DB_PASSWORD" --single-transaction --quick --no-tablespaces dosimetros_db \
  | gzip > "$FILE"

echo "Backup creado: $FILE"

# Rotación: deja solo los 14 más recientes.
ls -1t "$DEST"/dosimetros_db-*.sql.gz 2>/dev/null | tail -n +15 | xargs -r rm -f
