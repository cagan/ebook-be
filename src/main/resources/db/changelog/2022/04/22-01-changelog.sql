-- liquibase formatted sql

-- changeset cagan:1650577583780-1
CREATE TABLE books
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    title      VARCHAR(50)           NOT NULL,
    author     VARCHAR(50)           NULL,
    genre      VARCHAR(255)          NULL,
    height     INT                   NULL,
    publisher  VARCHAR(255)          NULL,
    created_at datetime              NULL,
    updated_at datetime              NULL,
    CONSTRAINT pk_books PRIMARY KEY (id)
);

-- changeset cagan:1650577583780-2
CREATE TABLE user
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    username VARCHAR(255)          NOT NULL,
    password VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

-- changeset cagan:1650577583780-3
ALTER TABLE books
    ADD CONSTRAINT uc_books_title UNIQUE (title);

-- changeset cagan:1650577583780-4
drop table book;