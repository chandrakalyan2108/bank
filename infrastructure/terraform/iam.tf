# IAM role assumed by the EC2 instance so it can pull images from ECR
# without needing long-lived AWS keys stored on the host.

resource "aws_iam_role" "ec2_ecr_role" {
  name = "${var.project_name}-ec2-ecr-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecr_read_only" {
  role       = aws_iam_role.ec2_ecr_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
}

resource "aws_iam_instance_profile" "ec2_ecr_profile" {
  name = "${var.project_name}-ec2-ecr-profile"
  role = aws_iam_role.ec2_ecr_role.name
}

# ---- Dedicated IAM user for GitHub Actions to push images to ECR ----
resource "aws_iam_user" "github_actions" {
  name = "${var.project_name}-github-actions"
}

resource "aws_iam_user_policy_attachment" "github_actions_ecr_push" {
  user       = aws_iam_user.github_actions.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryPowerUser"
}

resource "aws_iam_access_key" "github_actions_key" {
  user = aws_iam_user.github_actions.name
}
