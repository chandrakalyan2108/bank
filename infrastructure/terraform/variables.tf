variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-south-1"
}

variable "project_name" {
  description = "Project name used as a prefix for resources"
  type        = string
  default     = "banking-app"
}

variable "ec2_instance_type" {
  description = "EC2 instance type for the application host"
  type        = string
  default     = "t3.medium"
}

variable "key_pair_name" {
  description = "Name of an existing EC2 key pair for SSH access"
  type        = string
}

variable "allowed_ssh_cidr" {
  description = "CIDR block allowed to SSH into the EC2 instance"
  type        = string
  default     = "0.0.0.0/0" # restrict this to your IP in production
}

variable "vpc_id" {
  description = "VPC ID to launch the EC2 instance in (defaults to the account's default VPC if left empty)"
  type        = string
  default     = ""
}

variable "subnet_id" {
  description = "Subnet ID to launch the EC2 instance in"
  type        = string
  default     = ""
}
