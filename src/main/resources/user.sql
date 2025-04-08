CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    lastLogin TIMESTAMP NOT NULL,
    token VARCHAR(255),
    active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS phones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(20) NOT NULL,
    cityCode VARCHAR(5) NOT NULL,
    countryCode VARCHAR(5) NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


INSERT INTO users (name, email, password, created, modified, lastLogin, token, active)
VALUES ('Juan Rodriguez', 'juan@rodriguez.org', 'hunter2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'dummy-jwt-token', TRUE);


INSERT INTO phones (number, cityCode, countryCode, user_id)
VALUES ('1234567', '1', '57', (SELECT id FROM users WHERE email = 'juan@rodriguez.org' LIMIT 1));