data "aws_vpc" "selected" {
  id      = var.vpc_id != "" ? var.vpc_id : null
  default = var.vpc_id == "" ? true : null
}

data "aws_subnets" "selected" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.selected.id]
  }
}

# Amazon Linux 2023 AMI (x86_64) - latest
data "aws_ami" "al2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}

resource "aws_security_group" "app_sg" {
  name        = "${var.project_name}-sg"
  description = "Allow SSH, HTTP frontend and backend API traffic"
  vpc_id      = data.aws_vpc.selected.id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.allowed_ssh_cidr]
  }

  ingress {
    description     = "Frontend (HTTP) - from ALB only"
    from_port       = 80
    to_port         = 80
    protocol        = "tcp"
    security_groups = [aws_security_group.alb_sg.id]
  }

  ingress {
    description     = "Backend REST API - from ALB only"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-sg" }
}

resource "aws_instance" "app_server" {
  ami                    = data.aws_ami.al2023.id
  instance_type          = var.ec2_instance_type
  key_name               = var.key_pair_name
  subnet_id              = var.subnet_id != "" ? var.subnet_id : data.aws_subnets.selected.ids[0]
  vpc_security_group_ids = [aws_security_group.app_sg.id]
  iam_instance_profile   = aws_iam_instance_profile.ec2_ecr_profile.name

  root_block_device {
    volume_size = 20
    volume_type = "gp3"
  }

  user_data = <<-EOF
    #!/bin/bash
    set -e
    dnf update -y
    dnf install -y docker
    systemctl enable docker
    systemctl start docker
    usermod -aG docker ec2-user

    # docker compose plugin
    mkdir -p /usr/local/lib/docker/cli-plugins
    curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
      -o /usr/local/lib/docker/cli-plugins/docker-compose
    chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

    mkdir -p /opt/banking-app/database
  EOF

  tags = {
    Name = "${var.project_name}-server"
  }
}

output "ec2_public_ip" {
  value       = aws_instance.app_server.public_ip
  description = "Public IP of the EC2 host - point EC2_HOST GitHub secret at this"
}

output "ecr_backend_url" {
  value = aws_ecr_repository.backend.repository_url
}

output "ecr_frontend_url" {
  value = aws_ecr_repository.frontend.repository_url
}

output "github_actions_access_key_id" {
  value     = aws_iam_access_key.github_actions_key.id
  sensitive = true
}

output "github_actions_secret_access_key" {
  value     = aws_iam_access_key.github_actions_key.secret
  sensitive = true
}
