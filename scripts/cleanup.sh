#!/usr/bin/env bash
# Stop and remove local containers, volumes and dangling images.
set -euo pipefail

echo "Stopping containers..."
docker compose down -v

echo "Pruning dangling images..."
docker image prune -f

echo "Cleanup complete."
