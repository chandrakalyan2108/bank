#!/usr/bin/env bash
# Build backend and frontend Docker images locally
set -euo pipefail

echo "Building backend image..."
docker build -t banking-app-backend:local ./backend

echo "Building frontend image..."
docker build -t banking-app-frontend:local ./frontend

echo "Build complete. Run 'docker compose up' to start the stack."
