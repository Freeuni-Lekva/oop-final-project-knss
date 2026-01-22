package com.example.problems.Filters;

import com.example.registration.DTO.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterStatusTodoTest {

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

            stmt.execute("""
                CREATE TABLE problems (
                  id INT PRIMARY KEY,
                  problem_title VARCHAR(128) NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE submissions (
                  user_id INT NOT NULL,
                  problem_id INT NOT NULL,
                  FOREIGN KEY (problem_id) REFERENCES problems(id)
                );
            """);

            stmt.execute("""
                INSERT INTO problems (id, problem_title) VALUES
                  (1, 'First'),
                  (2, 'Second'),
                  (3, 'Third');
            """);

            stmt.execute("""
                INSERT INTO submissions (user_id, problem_id) VALUES
                  (42, 2);
            """);
        }
    }

    @Test
    void testStatusTodoReturnsOnlyUnsubmittedProblems() throws Exception {
        User user = new User();
        user.setId(42);

        FilterStatusTodo filter = new FilterStatusTodo(user);

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = filter.toSQLPreparedStatement(conn);
             ResultSet rs = ps.executeQuery()) {

            List<Integer> ids = new ArrayList<>();
            List<String> statuses = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getInt("id"));
                statuses.add(rs.getString("status"));
            }

            // should only get problems 1 and 3 back
            assertEquals(2, ids.size(), "should return exactly 2 problems");
            assertTrue(ids.contains(1), "should include problem 1");
            assertTrue(ids.contains(3), "should include problem 3");

            // both rows must carry the literal status "Todo"
            assertEquals(2,
                    statuses.stream().filter(s -> s.equals("Todo")).count(),
                    "each returned row must have status = 'Todo'");
        }
    }

    @Test
    void testStatusTodoWhenAllProblemsSubmitted() throws Exception {
        User user = new User();
        user.setId(99);

        FilterStatusTodo filter = new FilterStatusTodo(user);

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = filter.toSQLPreparedStatement(conn);
             ResultSet rs = ps.executeQuery()) {

            List<Integer> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

            assertEquals(3, ids.size(),
                    "user 99 with no submissions should see all 3 problems");
            assertTrue(ids.containsAll(List.of(1, 2, 3)),
                    "should see problems 1, 2, 3");
        }
    }

    @Test
    void testSqlStatementHasOnePlaceholder() {
        User user = new User();
        user.setId(12345);

        FilterStatusTodo filter = new FilterStatusTodo(user);
        String sql = filter.toSQLStatement();

        long count = sql.chars().filter(ch -> ch == '?').count();
        assertEquals(1, count, "there should be exactly one '?' placeholder");
    }
}
