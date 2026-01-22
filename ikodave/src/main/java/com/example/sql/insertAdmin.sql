USE Ikodave;
INSERT INTO users
    (role_id,
     mail,
     username,
     password_hash,
     is_verified,
     verification_token,
     verification_token_expiry,
     register_date) VALUES
(
    1,
    '',
    'admin',
    '$2a$10$GZcnnxdMw8MYt4.cgqUkiuN3cWFkTLL.kYpB7o7sA.1V2Q5BYhj0e',
    TRUE,
    NULL,
    NULL,
    NOW()
);

-- password = 'admin'