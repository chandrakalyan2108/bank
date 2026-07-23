USE banking;

INSERT INTO roles (role_name) VALUES ('CASHIER'), ('BANK_MANAGER')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- default passwords are bcrypt hash of "password123" - change before production use
INSERT INTO users (username, password, role_id, status)
SELECT 'cashier1', '$2b$10$nMASxgrEK9oB3ynVGSEid.0HTYhHaSuCmPeUDe8m8Q7POMO9pylDe', id, 'ACTIVE'
FROM roles WHERE role_name = 'CASHIER'
ON DUPLICATE KEY UPDATE username = VALUES(username), password = VALUES(password);

INSERT INTO users (username, password, role_id, status)
SELECT 'manager1', '$2b$10$nMASxgrEK9oB3ynVGSEid.0HTYhHaSuCmPeUDe8m8Q7POMO9pylDe', id, 'ACTIVE'
FROM roles WHERE role_name = 'BANK_MANAGER'
ON DUPLICATE KEY UPDATE username = VALUES(username), password = VALUES(password);
