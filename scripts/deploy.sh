#!/usr/bin/env bash
# Manual deploy helper - build, tag, push to ECR, then SSH to EC2 and restart.
# Usage: ./scripts/deploy.sh <ecr-registry> <aws-region> <ec2-host> <ec2-user> <ssh-key-path>
set -euo pipefail

ECR_REGISTRY=${1:?ECR registry required, e.g. 123456789012.dkr.ecr.ap-south-1.amazonaws.com}
AWS_REGION=${2:?AWS region required}
EC2_HOST=${3:?EC2 host/IP required}
EC2_USER=${4:-ec2-user}
SSH_KEY=${5:?Path to SSH private key required}

TAG=$(git rev-parse --short HEAD)

echo "Logging in to ECR..."
aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$ECR_REGISTRY"

echo "Building & pushing backend ($TAG)..."
docker build -t "$ECR_REGISTRY/banking-app-backend:$TAG" -t "$ECR_REGISTRY/banking-app-backend:latest" ./backend
docker push "$ECR_REGISTRY/banking-app-backend:$TAG"
docker push "$ECR_REGISTRY/banking-app-backend:latest"

echo "Building & pushing frontend ($TAG)..."
docker build -t "$ECR_REGISTRY/banking-app-frontend:$TAG" -t "$ECR_REGISTRY/banking-app-frontend:latest" ./frontend
docker push "$ECR_REGISTRY/banking-app-frontend:$TAG"
docker push "$ECR_REGISTRY/banking-app-frontend:latest"

echo "Deploying to EC2 ($EC2_HOST)..."
scp -i "$SSH_KEY" docker-compose.prod.yml database/schema.sql database/data.sql "$EC2_USER@$EC2_HOST:/opt/banking-app/"

ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" bash -s << REMOTE
  set -e
  cd /opt/banking-app
  export ECR_REGISTRY=$ECR_REGISTRY
  export IMAGE_TAG=$TAG
  aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY
  docker compose -f docker-compose.prod.yml pull
  docker compose -f docker-compose.prod.yml up -d
  docker image prune -f
REMOTE

echo "Deploy complete."
