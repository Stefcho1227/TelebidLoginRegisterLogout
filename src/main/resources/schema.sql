CREATE DATABASE IF NOT EXISTS regapp
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'regapp'@'localhost'
  IDENTIFIED BY 'ChangeMe123!';
GRANT ALL PRIVILEGES ON regapp.* TO 'regapp'@'localhost';
FLUSH PRIVILEGES;

USE regapp;

CREATE TABLE IF NOT EXISTS users (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    first_name    VARCHAR(60)  NOT NULL,
    last_name     VARCHAR(60)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_users_email (email)
) ENGINE = InnoDB;
