package com.example.user_profile.dao;

import com.example.registration.DTO.User;
import com.example.submissions.DTO.SubmissionVerdict;
import org.apache.commons.dbcp2.BasicDataSource;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserSubmissionStatsDAOTest {

    private static BasicDataSource dataSource;
    private static UserStatsDAO dao;

    @BeforeAll
    static void setup() throws Exception {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");

        dao = new SQLUserStatsDAO(dataSource); // or whatever class contains the method

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE users (
                    id INT PRIMARY KEY,
                    username VARCHAR(255),
                    password VARCHAR(255)
                );
            """);
            stmt.execute("""
                CREATE TABLE problems (
                    id INT PRIMARY KEY
                );
            """);
            stmt.execute("""
                CREATE TABLE submissions (
                    id INT PRIMARY KEY,
                    user_id INT,
                    problem_id INT,
                    verdict_id INT
                );
            """);
        }
    }

    @BeforeEach
    void seedData() throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO users VALUES (1, 'john', '1234');");
            stmt.execute("INSERT INTO problems VALUES (101), (102), (103);");
            stmt.execute("INSERT INTO submissions VALUES " +
                    "(1, 1, 101, 2), " +  // status 2
                    "(2, 1, 101, 2), " +  // same problem, same status
                    "(3, 1, 102, 2), " +  // different problem, same status
                    "(4, 1, 103, 3);");   // different status
        }
    }

    @AfterEach
    void clearData() throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM submissions");
            stmt.execute("DELETE FROM problems");
            stmt.execute("DELETE FROM users");
        }
    }

    @Test
    void testGetStatusProblemsCount() {
        User user = new User();
        user.setId(1);
        SubmissionVerdict verdict = new SubmissionVerdict(2, "ACCEPTED");

        int count = dao.getSubmittedProblemCountByVerdict(user, verdict);
        assertEquals(3, count); // 101 and 102 are unique problems with status 2
    }

    @Test
    void testReturnsZeroIfNoMatches() {
        User user = new User();
        user.setId(1);
        SubmissionVerdict verdict = new SubmissionVerdict(999, "NON_EXISTENT");

        int count = dao.getSubmittedProblemCountByVerdict(user, verdict);
        assertEquals(0, count);
    }
}