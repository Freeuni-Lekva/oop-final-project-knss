CREATE SCHEMA IF NOT EXISTS Ikodave;

USE Ikodave;


CREATE TABLE IF NOT EXISTS user_rank
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    user_rank VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS users
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    rank_id       INT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    register_date DATE,
    FOREIGN KEY (rank_id) REFERENCES user_rank (id)
);


CREATE TABLE IF NOT EXISTS problem_difficulty
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    difficulty VARCHAR(16) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS problem_topic
(
    id    INT AUTO_INCREMENT PRIMARY KEY,
    topic VARCHAR(16) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS problem_status
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(16) UNIQUE NOT NULL
);

-- problems
CREATE TABLE IF NOT EXISTS problems
(
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    problem_title       VARCHAR(100) UNIQUE NOT NULL,
    problem_description TEXT,
    difficulty_id       INT,
    create_date         DATE,
    time_limit          LONG DEFAULT 20000,
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
    id            INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT,
    problem_id    INT,
    status_id     INT,
    solution_code TEXT,
    submit_date   DATE,
    log           TEXT,
    FOREIGN KEY (problem_id) REFERENCES problems (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (status_id) REFERENCES problem_status (id)
);

CREATE TABLE IF NOT EXISTS test_cases
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    problem_id     INT,
    problem_input  TEXT,
    problem_output TEXT,
    order_num      INT,
    FOREIGN KEY (problem_id) REFERENCES problems (id)
);
