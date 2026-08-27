#!/usr/bin/env bash
set -euo pipefail

cleanup(){ docker compose down -v; }
trap cleanup EXIT

echo "[1/5] Running Maven verification"
mvn -B clean verify

echo "[2/5] Building and starting Docker stack"
docker compose up --build -d

echo "[3/5] Waiting for application health"
for i in {1..60}; do
  if curl -fsS http://localhost:8080/actuator/health >/dev/null; then break; fi
  sleep 2
  if [ "$i" -eq 60 ]; then echo "Application did not become healthy"; exit 1; fi
done

echo "[4/5] Verifying health endpoint"
curl -fsS http://localhost:8080/actuator/health

echo "[5/5] Runtime smoke test complete"
echo "PASS: Maven build, tests, Docker build, PostgreSQL startup, Flyway startup, and health check succeeded."
