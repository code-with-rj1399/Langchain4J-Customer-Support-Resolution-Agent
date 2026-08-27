#!/usr/bin/env bash
set -euo pipefail

POSTGRES_CONTAINER=${POSTGRES_CONTAINER:-$(docker compose ps -q postgres)}
DB_NAME=${POSTGRES_DB:-customer_support}
DB_USER=${POSTGRES_USER:-customer_support}

if [ -z "$POSTGRES_CONTAINER" ]; then
  echo "PostgreSQL container is not running. Start it with: docker compose up -d postgres"
  exit 1
fi

docker exec "$POSTGRES_CONTAINER" psql -U "$DB_USER" -d postgres -c "DROP DATABASE IF EXISTS $DB_NAME;"
docker exec "$POSTGRES_CONTAINER" psql -U "$DB_USER" -d postgres -c "CREATE DATABASE $DB_NAME;"

echo "Database '$DB_NAME' reset successfully. Restart the application to run Flyway migrations."
