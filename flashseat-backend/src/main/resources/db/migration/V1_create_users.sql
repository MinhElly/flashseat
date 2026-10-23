CREATE TABLE users {
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
} ENGINE = InnoDB;