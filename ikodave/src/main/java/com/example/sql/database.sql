CREATE SCHEMA IF NOT EXISTS Ikodave;

USE Ikodave;


CREATE TABLE IF NOT EXISTS user_role
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS users
(
    id                              INT AUTO_INCREMENT PRIMARY KEY,
    role_id                         INT,
    mail                            VARCHAR(64)  NOT NULL,
    username                        VARCHAR(64)  NOT NULL UNIQUE,
    password_hash                   VARCHAR(256) NOT NULL,
    is_verified                     BOOLEAN NOT NULL DEFAULT FALSE,
    verification_token              VARCHAR(64) UNIQUE,
    verification_token_expiry       DATETIME,
    register_date                   TIMESTAMP NOT NULL,
    FOREIGN KEY (role_id)           REFERENCES user_role (id)
);

CREATE TABLE IF NOT EXISTS problem_status
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    status      VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS problem_difficulty
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    difficulty VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS problem_topic
(
    id    INT AUTO_INCREMENT PRIMARY KEY,
    topic VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS submission_verdict
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    verdict VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS code_language
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    language VARCHAR(32) UNIQUE NOT NULL
);

-- problems
CREATE TABLE IF NOT EXISTS problems
(
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    problem_title       VARCHAR(128) UNIQUE NOT NULL,
    problem_description TEXT NOT NULL,
    difficulty_id       INT,
    time_limit          BIGINT NOT NULL,
    memory_limit        BIGINT,
    input_spec          TEXT NOT NULL,
    output_spec         TEXT NOT NULL,
    create_date         TIMESTAMP NOT NULL,
    FOREIGN KEY (difficulty_id) REFERENCES problem_difficulty (id)
);

CREATE TABLE IF NOT EXISTS problem_many_to_many_topic
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    problem_id INT,
    topic_id   INT,
    FOREIGN KEY (problem_id) REFERENCES problems (id),
    FOREIGN KEY (topic_id) REFERENCES problem_topic (id)
);

CREATE TABLE IF NOT EXISTS submissions
(
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    user_id             INT,
    problem_id          INT,
    verdict_id          INT,
    solution_code       TEXT,
    code_language_id    INT NOT NULL,
    time                BIGINT NOT NULL,
    memory              BIGINT,
    submit_date         TIMESTAMP NOT NULL,
    log                 TEXT,
    FOREIGN KEY (problem_id) REFERENCES problems (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (verdict_id) REFERENCES submission_verdict (id),
    FOREIGN KEY (code_language_id) REFERENCES code_language (id)
);

CREATE TABLE IF NOT EXISTS test_cases
(
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    problem_id          INT,
    problem_input       MEDIUMTEXT,
    problem_output      MEDIUMTEXT,
    test_number         INT,
    FOREIGN KEY (problem_id) REFERENCES problems (id)
);