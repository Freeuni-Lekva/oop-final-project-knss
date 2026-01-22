package com.example.problems.Filters;

import com.example.registration.DTO.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterStatusSolvedTest {

    private static DataSource ds;

    @BeforeAll
    static void setupDatabase() throws Exception {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        bds.setUsername("sa");
        bds.setPassword("");
        bds.setDriverClassName("org.h2.Driver");
        ds = bds;

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            // problems table
            stmt.execute("""
                CREATE TABLE problems (
                  id INT PRIMARY KEY,
                  problem_title VARCHAR(128) NOT NULL
                );
            """);

            // submission_verdict table
            stmt.execute("""
                CREATE TABLE submission_verdict (
                  id INT PRIMARY KEY,
                  verdict VARCHAR(64) NOT NULL
                );
            """);

            // submissions table
            stmt.execute("""
                CREATE TABLE submissions (
                  user_id INT NOT NULL,
                  problem_id INT NOT NULL,
                  verdict_id INT NOT NULL,
                  FOREIGN KEY (problem_id) REFERENCES problems(id),
                  FOREIGN KEY (verdict_id) REFERENCES submission_verdict(id)
                );
            """);

            // insert three problems
            stmt.execute("""
                INSERT INTO problems (id, problem_title) VALUES
                  (1, 'Alpha'),
                  (2, 'Beta'),
                  (3, 'Gamma');
            """);

            // insert verdict types
            stmt.execute("""
                INSERT INTO submission_verdict (id, verdict) VALUES
                  (1, 'Accepted'),
                  (2, 'WrongAnswer'),
                  (3, 'TimeLimit');
            """);

            // user 42: one accepted on problem 1, one WA on problem 2, two accepted on problem 3
            stmt.execute("""
                INSERT INTO submissions (user_id, problem_id, verdict_id) VALUES
                  (42, 1, 1),
                  (42, 2, 2),
                  (42, 3, 1),
                  (42, 3, 1);
            """);
        }
    }

    @Test
    void testStatusSolvedReturnsOnlyAcceptedProblems() throws Exception {
        User user = new User();
        user.setId(42);

        FilterStatusSolved filter = new FilterStatusSolved(user);

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = filter.toSQLPreparedStatement(conn);
             ResultSet rs = ps.executeQuery()) {

            List<Integer> ids = new ArrayList<>();
            List<String> statuses = new ArrayList<>();

            while (rs.next()) {
                ids.add(rs.getInt("id"));
                statuses.add(rs.getString("status"));
            }

            // Should only include problems 1 & 3 (distinct)
            assertEquals(2, ids.size(), "should return exactly 2 solved problems");
            assertTrue(ids.contains(1), "should include problem 1");
            assertTrue(ids.contains(3), "should include problem 3");

            // Every row must carry the literal status "Solved"
            assertEquals(2,
                    statuses.stream().filter(s -> s.equals("Solved")).count(),
                    "each returned row must have status = 'Solved'");
        }
    }

    @Test
    void testStatusSolvedWhenNoAcceptedSubmissions() throws Exception {
        User user = new User();
        user.setId(99);  // no submissions at all

        FilterStatusSolved filter = new FilterStatusSolved(user);

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = filter.toSQLPreparedStatement(conn);
             ResultSet rs = ps.executeQuery()) {

            assertFalse(rs.next(), "user 99 should see no solved problems");
        }
    }

    @Test
    void testSqlStatementHasTwoPlaceholders() {
        User user = new User();
        user.setId(555);

        FilterStatusSolved filter = new FilterStatusSolved(user);
        String sql = filter.toSQLStatement();

        long count = sql.chars().filter(ch -> ch == '?').count();
        assertEquals(2, count, "there should be exactly two '?' placeholders");
    }
}
