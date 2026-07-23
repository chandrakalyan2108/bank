resource "aws_ecr_repository" "backend" {
  name                 = "${var.project_name}-backend"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "frontend" {
  name                 = "${var.project_name}-frontend"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

# Keep only the last 10 images per repo to control storage cost
locals {
  retain_last_10_policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Keep last 10 images"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 10
      }
      action = { type = "expire" }
    }]
  })
}

resource "aws_ecr_lifecycle_policy" "backend_policy" {
  repository = aws_ecr_repository.backend.name
  policy     = local.retain_last_10_policy
}

resource "aws_ecr_lifecycle_policy" "frontend_policy" {
  repository = aws_ecr_repository.frontend.name
  policy     = local.retain_last_10_policy
}
