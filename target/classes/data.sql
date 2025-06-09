-- Insert admin user (password: password) if not exists
INSERT INTO users (name, username, email, password, first_name, last_name, role, enabled, email_verified)
SELECT 'Admin User', 'admin@cursorheat.com', 'admin@cursorheat.com', '$2a$10$7QJ8Qw8Qw8Qw8Qw8Qw8QwOQw8Qw8Qw8Qw8Qw8Qw8Qw8Qw8Qw8', 'Admin', 'User', 'ADMIN', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@cursorheat.com');

-- Insert test user (password: password) if not exists
INSERT INTO users (name, username, email, password, first_name, last_name, role, enabled, email_verified)
SELECT 'Test User', 'test@example.com', 'test@example.com', '$2a$10$7QJ8Qw8Qw8Qw8Qw8Qw8QwOQw8Qw8Qw8Qw8Qw8Qw8Qw8Qw8Qw8', 'Test', 'User', 'USER', TRUE, FALSE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'test@example.com');

-- Insert test project if not exists
INSERT INTO projects (name, api_key, user_id, active, domain)
SELECT 'Test Project', 'test-api-key-123', id, TRUE, 'example.com'
FROM users
WHERE email = 'admin@cursorheat.com'
AND NOT EXISTS (SELECT 1 FROM projects WHERE api_key = 'test-api-key-123'); 