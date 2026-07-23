# 🏦 Banking Application

A full-stack banking application with two roles — **Cashier** (login, create customer/account, deposit, withdraw)
and **Bank Manager** (approve/reject pending accounts) — built on a Spring Boot REST API, a React frontend,
and MySQL, containerized with Docker and deployed via GitHub Actions to **Amazon ECR + an EC2 host**.

## Project Structure

```
banking-application/
├── .github/workflows/       # CI (build/test) + CD (build → ECR → EC2)
│   ├── ci.yml
│   └── deploy.yml
├── backend/                 # Spring Boot REST API
│   └── src/main/java/com/bank/app/
│       ├── controller/       # REST endpoints
│       ├── service/          # Business logic
│       ├── repository/       # Spring Data JPA repositories
│       ├── entity/           # JPA entities (users, customers, accounts, approvals, transactions)
│       ├── dto/               # Request/response objects
│       ├── security/          # JWT auth filter & service
│       ├── config/            # Spring Security config
│       └── exception/         # Global exception handling
├── frontend/                 # React SPA (Cashier & Manager dashboards)
├── database/                 # schema.sql + seed data.sql
├── infrastructure/terraform/ # ECR repos, IAM roles, EC2 instance + security group
├── docker-compose.yml         # Local dev (builds from source)
├── docker-compose.prod.yml    # Production (pulls images from ECR) - used on EC2
└── scripts/                  # build.sh, deploy.sh, cleanup.sh
```

## System Architecture

```
Client (Browser) → React Frontend (nginx:3000/80) → Spring Boot REST API (:8080) → MySQL (:3306)
                                                            │
                                                       JWT Authentication
```

## User Roles

| Role         | Permissions |
|--------------|-------------|
| **Cashier**      | Login, create customer, create account (→ PENDING), deposit, withdraw, view customers/accounts. Cannot approve accounts. |
| **Bank Manager** | Login, view pending accounts, approve/reject accounts, view all accounts/reports. |

## Account Lifecycle

```
Cashier creates account → status = PENDING → Manager reviews → Approve → ACTIVE
                                                              → Reject  → REJECTED
```

## Request Flow

```
[Cashier logs in] ──(POST /api/auth/login)──► [AuthController] ──► JWT issued
       │
       ▼
[Cashier: CashierDashboard.js] ──(POST /api/customers)──► [CustomerController] ──► [CustomerService]
       │                                                                                  │
       │                                                                                  ▼
       │                                                                  [Database: Customer row created]
       │
       ▼
[Cashier submits account form] ──(POST /api/accounts)──► [AccountController] ──► [AccountService]
                                                                                        │
                                                                                        ▼
                                                                     [Database: Account, status = PENDING,
                                                                      balance = 0, linked to customerId]
                                                                                        │
       ┌────────────────────────────────────────────────────────────────────────────────┘
       ▼
[Manager: ManagerDashboard.js] ◄──(GET /api/manager/accounts/pending)──── [ManagerController]
       │
       ├──(PUT /api/manager/accounts/{accountNo}/approve)──► [AccountService.approveAccount]
       │                                                            ├──► status = ACTIVE
       │                                                            └──► Approval row logged (APPROVED)
       │
       └──(PUT /api/manager/accounts/{accountNo}/reject)───► [AccountService.rejectAccount]
                                                                    ├──► status = REJECTED
                                                                    └──► Approval row logged (REJECTED)

[Cashier performs deposit] ──(POST /api/transactions/deposit)──► [TransactionController]
                                                                          │
                                                                          ▼
[Cashier performs withdrawal] ─(POST /api/transactions/withdraw)► [TransactionService]
                                                                          │
                                                                          ├──► Validates account is ACTIVE
                                                                          ├──► Updates Account.balance in DB
                                                                          └──► Transaction row recorded
```

> Note: there's no auto-generated customer code (e.g. `CUST-10001`) or automatic starting balance — `customerId` is a plain auto-incrementing ID from the `customers` table, and every new account starts at `balance = 0` until a cashier makes a deposit. Approve/reject also live under the manager-only `/api/manager/**` routes, not `/api/accounts/**`.

## Running Locally

Requires Docker & Docker Compose.

```bash
git clone <your-repo-url>
cd banking-application
docker compose up --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- MySQL: localhost:3306 (root/root)

Seed users (see `database/data.sql`) — change these before any real deployment:
- `cashier1` / `password123` (role: CASHIER)
- `manager1` / `password123` (role: BANK_MANAGER)

## Deployment: GitHub Actions → Docker Build → ECR → EC2

### 1. Provision infrastructure with Terraform

```bash
cd infrastructure/terraform
terraform init
terraform apply \
  -var="key_pair_name=<your-ec2-keypair-name>" \
  -var="aws_region=ap-south-1"
```

This creates:
- Two ECR repositories: `banking-app-backend`, `banking-app-frontend`
- An IAM instance profile so the EC2 host can pull from ECR without static keys
- A dedicated IAM user + access key for GitHub Actions to **push** to ECR
- An EC2 instance (Amazon Linux 2023) with Docker + Docker Compose pre-installed via `user_data`
- An **Application Load Balancer** with two target groups:
  - `frontend-tg` → instance port 80, health check on `/`
  - `backend-tg` → instance port 8080, health check on `/actuator/health`, routed via a `/api/*` listener rule
- A security group where ports 80/8080 only accept traffic **from the ALB**, not directly from the internet (SSH is still open per `allowed_ssh_cidr`)

Note the Terraform outputs: `ec2_public_ip`, `alb_dns_name`, `ecr_backend_url`, `ecr_frontend_url`, and the (sensitive) GitHub Actions access key/secret.

Use `alb_dns_name` as the public entry point going forward (e.g. `http://banking-app-alb-123456789.ap-south-1.elb.amazonaws.com`) instead of the raw EC2 IP — the frontend routes at `/` and the API at `/api/*` both go through it.

### 2. On the EC2 host (one-time setup)

```bash
ssh -i <key.pem> ec2-user@<ec2_public_ip>
sudo mkdir -p /opt/banking-app/database
sudo chown ec2-user:ec2-user -R /opt/banking-app
```

### 3. Add GitHub repository secrets

Settings → Secrets and variables → Actions:

| Secret | Value |
|---|---|
| `AWS_ACCESS_KEY_ID` | from `terraform output github_actions_access_key_id` |
| `AWS_SECRET_ACCESS_KEY` | from `terraform output github_actions_secret_access_key` |
| `AWS_REGION` | e.g. `ap-south-1` |
| `ECR_REGISTRY` | e.g. `123456789012.dkr.ecr.ap-south-1.amazonaws.com` |
| `EC2_HOST` | `terraform output ec2_public_ip` |
| `EC2_USER` | `ec2-user` |
| `EC2_SSH_KEY` | contents of your EC2 private key (.pem) |
| `DB_ROOT_PASSWORD` | a strong MySQL root password |
| `JWT_SECRET` | a long random string for signing JWTs |
| `REACT_APP_API_URL` | `http://<alb_dns_name>/api` (from `terraform output alb_dns_name`) |

### 4. Push to `main`

`.github/workflows/deploy.yml` runs on every push to `main`:

1. **Build & tag** backend and frontend Docker images (tagged with both the short git SHA and `latest`)
2. **Push** both images to Amazon ECR
3. **Copy** `docker-compose.prod.yml` and the database scripts to `/opt/banking-app` on the EC2 host over SSH
4. **SSH in**, write a `.env` file with the image tag/registry/secrets, `docker compose pull` the new images, and `docker compose up -d` to roll out with zero manual steps

Rollback: re-run the workflow from an older commit, or SSH in and set `IMAGE_TAG` in `.env` to a previous short SHA, then `docker compose -f docker-compose.prod.yml up -d`.

### CI (`ci.yml`)

Runs on pushes to `develop`/`feature/**` and PRs into `main`: compiles & tests the backend (Maven) and builds the frontend (npm), so branches are validated before merging to `main` triggers a deploy.

## Security Notes

- Passwords are hashed with BCrypt; JWTs are signed with HS256 and carry the user's role as a claim
- `SecurityConfig` restricts `/api/manager/**` to `ROLE_BANK_MANAGER` and `/api/cashier/**` to `ROLE_CASHIER`
- Rotate `JWT_SECRET` and `DB_ROOT_PASSWORD` before using outside of local development, and restrict `allowed_ssh_cidr` in Terraform to your own IP rather than `0.0.0.0/0`
