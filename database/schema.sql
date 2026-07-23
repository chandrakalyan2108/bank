-- ============================================================
-- Banking Application - MySQL Schema
-- ============================================================
CREATE DATABASE IF NOT EXISTS banking;
USE banking;

-- ---------- roles ----------
CREATE TABLE IF NOT EXISTS roles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(30) NOT NULL UNIQUE   -- CASHIER, BANK_MANAGER
);

-- ---------- users ----------
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(60)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role_id     BIGINT NOT NULL,
    status      VARCHAR(20) DEFAULT 'ACTIVE',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ---------- customers ----------
CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name  VARCHAR(60) NOT NULL,
    last_name   VARCHAR(60) NOT NULL,
    mobile      VARCHAR(15) NOT NULL,
    email       VARCHAR(120),
    aadhaar     VARCHAR(20) UNIQUE,
    created_by  BIGINT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customers_user FOREIGN KEY (created_by) REFERENCES users(id)
);

-- ---------- accounts ----------
CREATE TABLE IF NOT EXISTS accounts (
    account_no   BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id  BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL,      -- SAVINGS, CURRENT
    balance      DECIMAL(15,2) DEFAULT 0.00,
    status       VARCHAR(20) DEFAULT 'PENDING', -- PENDING, ACTIVE, REJECTED
    created_by   BIGINT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CONSTRAINT fk_accounts_user FOREIGN KEY (created_by) REFERENCES users(id)
);

-- ---------- approvals ----------
CREATE TABLE IF NOT EXISTS approvals (
    approval_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_no    BIGINT NOT NULL,
    approved_by   BIGINT,
    status        VARCHAR(20) NOT NULL,     -- APPROVED, REJECTED
    remarks       VARCHAR(255),
    approved_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_approvals_account FOREIGN KEY (account_no) REFERENCES accounts(account_no),
    CONSTRAINT fk_approvals_user FOREIGN KEY (approved_by) REFERENCES users(id)
);

-- ---------- transactions ----------
CREATE TABLE IF NOT EXISTS transactions (
    txn_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_no  BIGINT NOT NULL,
    txn_type    VARCHAR(20) NOT NULL,       -- DEPOSIT, WITHDRAW
    amount      DECIMAL(15,2) NOT NULL,
    txn_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cashier_id  BIGINT,
    CONSTRAINT fk_txn_account FOREIGN KEY (account_no) REFERENCES accounts(account_no),
    CONSTRAINT fk_txn_user FOREIGN KEY (cashier_id) REFERENCES users(id)
);
