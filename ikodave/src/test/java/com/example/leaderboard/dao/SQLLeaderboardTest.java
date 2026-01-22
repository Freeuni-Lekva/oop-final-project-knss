package com.example.leaderboard.dao;

import com.example.leaderboard.dto.UserWithScore;
import com.example.constants.DatabaseConstants;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLLeaderboardDAOTest {

    private static BasicDataSource dataSource;
    private SQLLeaderboardDAO leaderboardDAO;
    private Connection connection;

    @BeforeAll
    static void setUpDatabase() {
        // Create H2 in-memory database with MySQL mode to handle reserved keywords
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        // Set connection pool properties
        dataSource.setInitialSize(5);
        dataSource.setMaxTotal(10);
        dataSource.setMaxIdle(5);
        dataSource.setMinIdle(2);
    }

    @BeforeEach
    void setUp() throws SQLException {
        leaderboardDAO = new SQLLeaderboardDAO(dataSource);
        connection = dataSource.getConnection();

        // Create tables
        createTables();

        // Insert test data
        insertTestData();
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS " + DatabaseConstants.Submissions.TABLE_NAME);
            stmt.execute("DROP TABLE IF EXISTS " + DatabaseConstants.Users.TABLE_NAME);
            stmt.execute("DROP TABLE IF EXISTS " + DatabaseConstants.SubmissionVerdict.TABLE_NAME);
        }
        connection.close();
    }

    @AfterAll
    static void closeDataSource() throws SQLException {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE " + DatabaseConstants.Users.TABLE_NAME + " (" +
                    DatabaseConstants.Users.COL_ID + " INT PRIMARY KEY, " +
                    DatabaseConstants.Users.COL_USERNAME + " VARCHAR(100) NOT NULL UNIQUE" +
                    ")");

            stmt.execute("CREATE TABLE " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                    DatabaseConstants.SubmissionVerdict.COL_ID + " INT PRIMARY KEY, " +
                    DatabaseConstants.SubmissionVerdict.COL_VERDICT + " VARCHAR(50) NOT NULL" +
                    ")");

            stmt.execute("CREATE TABLE " + DatabaseConstants.Submissions.TABLE_NAME + " (" +
                    DatabaseConstants.Submissions.COL_ID + " INT PRIMARY KEY AUTO_INCREMENT, " +
                    DatabaseConstants.Submissions.COL_USER_ID + " INT NOT NULL, " +
                    DatabaseConstants.Submissions.COL_PROBLEM_ID + " INT NOT NULL, " +
                    DatabaseConstants.Submissions.COL_VERDICT_ID + " INT NOT NULL, " +
                    "FOREIGN KEY (" + DatabaseConstants.Submissions.COL_USER_ID + ") REFERENCES " +
                    DatabaseConstants.Users.TABLE_NAME + "(" + DatabaseConstants.Users.COL_ID + "), " +
                    "FOREIGN KEY (" + DatabaseConstants.Submissions.COL_VERDICT_ID + ") REFERENCES " +
                    DatabaseConstants.SubmissionVerdict.TABLE_NAME + "(" + DatabaseConstants.SubmissionVerdict.COL_ID + ")" +
                    ")");
        }
    }

    private void insertTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO " + DatabaseConstants.Users.TABLE_NAME +
                    " VALUES (1, 'Alice')");
            stmt.execute("INSERT INTO " + DatabaseConstants.Users.TABLE_NAME +
                    " VALUES (2, 'Bob')");
            stmt.execute("INSERT INTO " + DatabaseConstants.Users.TABLE_NAME +
                    " VALUES (3, 'Charlie')");
            stmt.execute("INSERT INTO " + DatabaseConstants.Users.TABLE_NAME +
                    " VALUES (4, 'Diana')");
            stmt.execute("INSERT INTO " + DatabaseConstants.Users.TABLE_NAME +
                    " VALUES (5, 'Eve')");

            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME +
                    " VALUES (1, 'Accepted')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME +
                    " VALUES (2, 'Wrong Answer')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME +
                    " VALUES (3, 'Time Limit Exceeded')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME +
                    " VALUES (4, 'Runtime Error')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME +
                    " VALUES (5, 'Compilation Error')");
        }
    }


    @Test
    void testGetUsersByScoreNoSubmissions() {
        List<UserWithScore> leaderboard = leaderboardDAO.getUsersByScore();

        assertNotNull(leaderboard);
        assertEquals(5, leaderboard.size(), "Should return all 5 users");

        for (UserWithScore user : leaderboard) {
            assertEquals(0, user.getScore(),
                    "User " + user.getUsername() + " should have score 0");
        }
    }

    @Test
    void testGetUsersByScoreWithAcceptedSubmissions() throws SQLException {
        insertSubmissions();

        List<UserWithScore> leaderboard = leaderboardDAO.getUsersByScore();

        assertNotNull(leaderboard);
        assertEquals(5, leaderboard.size(), "Should return all users");

        assertEquals("Alice", leaderboard.get(0).getUsername());
        assertEquals(3, leaderboard.get(0).getScore());

        assertEquals("Bob", leaderboard.get(1).getUsername());
        assertEquals(2, leaderboard.get(1).getScore());

        assertEquals("Charlie", leaderboard.get(2).getUsername());
        assertEquals(1, leaderboard.get(2).getScore());

        assertTrue(leaderboard.get(3).getScore() == 0);
        assertTrue(leaderboard.get(4).getScore() == 0);
    }

    @Test
    void testGetUsersByScoreDistinctProblems() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + DatabaseConstants.Submissions.TABLE_NAME +
                        " (" + DatabaseConstants.Submissions.COL_USER_ID + ", " +
                        DatabaseConstants.Submissions.COL_PROBLEM_ID + ", " +
                        DatabaseConstants.Submissions.COL_VERDICT_ID + ") VALUES (?, ?, ?)")) {

            for (int i = 0; i < 3; i++) {
                ps.setInt(1, 1);
                ps.setInt(2, 1);
                ps.setInt(3, 1);
                ps.executeUpdate();
            }
        }

        List<UserWithScore> leaderboard = leaderboardDAO.getUsersByScore();

        UserWithScore alice = leaderboard.stream()
                .filter(u -> u.getUsername().equals("Alice"))
                .findFirst()
                .orElseThrow();

        assertEquals(1, alice.getScore(),
                "Should count problem 1 only once despite multiple accepted submissions");
    }

    @Test
    @DisplayName("Test getUsersByScore - only counts accepted submissions")
    void testGetUsersByScoreOnlyAccepted() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + DatabaseConstants.Submissions.TABLE_NAME +
                        " (" + DatabaseConstants.Submissions.COL_USER_ID + ", " +
                        DatabaseConstants.Submissions.COL_PROBLEM_ID + ", " +
                        DatabaseConstants.Submissions.COL_VERDICT_ID + ") VALUES (?, ?, ?)")) {

            ps.setInt(1, 2);
            ps.setInt(2, 1);
            ps.setInt(3, 1);
            ps.executeUpdate();

            ps.setInt(1, 2);
            ps.setInt(2, 2);
            ps.setInt(3, 2);
            ps.executeUpdate();

            ps.setInt(1, 2);
            ps.setInt(2, 3);
            ps.setInt(3, 3);
            ps.executeUpdate();

            ps.setInt(1, 2);
            ps.setInt(2, 4);
            ps.setInt(3, 4);
            ps.executeUpdate();
        }

        List<UserWithScore> leaderboard = leaderboardDAO.getUsersByScore();

        UserWithScore bob = leaderboard.stream()
                .filter(u -> u.getUsername().equals("Bob"))
                .findFirst()
                .orElseThrow();

        assertEquals(1, bob.getScore(),
                "Should only count the accepted submission (problem 1)");
    }

    @Test
    void testGetUsersByScoreOrdering() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + DatabaseConstants.Submissions.TABLE_NAME +
                        " (" + DatabaseConstants.Submissions.COL_USER_ID + ", " +
                        DatabaseConstants.Submissions.COL_PROBLEM_ID + ", " +
                        DatabaseConstants.Submissions.COL_VERDICT_ID + ") VALUES (?, ?, ?)")) {

            for (int i = 1; i <= 5; i++) {
                ps.setInt(1, 5);
                ps.setInt(2, i);
                ps.setInt(3, 1);
                ps.executeUpdate();
            }

            for (int i = 1; i <= 3; i++) {
                ps.setInt(1, 4);
                ps.setInt(2, i);
                ps.setInt(3, 1);
                ps.executeUpdate();
            }

            ps.setInt(1, 3);
            ps.setInt(2, 1);
            ps.setInt(3, 1);
            ps.executeUpdate();
        }

        List<UserWithScore> leaderboard = leaderboardDAO.getUsersByScore();

        assertEquals("Eve", leaderboard.get(0).getUsername());
        assertEquals(5, leaderboard.get(0).getScore());

        assertEquals("Diana", leaderboard.get(1).getUsername());
        assertEquals(3, leaderboard.get(1).getScore());

        assertEquals("Charlie", leaderboard.get(2).getUsername());
        assertEquals(1, leaderboard.get(2).getScore());

        for (int i = 0; i < leaderboard.size() - 1; i++) {
            assertTrue(leaderboard.get(i).getScore() >= leaderboard.get(i + 1).getScore(),
                    "Scores should be in descending order");
        }
    }

    @Test
    void testGetUsersByScoreEmptyDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + DatabaseConstants.Submissions.TABLE_NAME);
            stmt.execute("DELETE FROM " + DatabaseConstants.Users.TABLE_NAME);
        }

        List<UserWithScore> leaderboard = leaderboardDAO.getUsersByScore();

        assertNotNull(leaderboard);
        assertTrue(leaderboard.isEmpty(), "Should return empty list when no users");
    }

    @Test
    void testGetUsersByScoreSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLLeaderboardDAO daoWithClosedDataSource = new SQLLeaderboardDAO(closedDataSource);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getUsersByScore());

        assertEquals("Error querying users by score", exception.getMessage());
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    @Test
    void testGetUsersByScoreSameScore() throws SQLException {
        // Arrange - Give multiple users the same score
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + DatabaseConstants.Submissions.TABLE_NAME +
                        " (" + DatabaseConstants.Submissions.COL_USER_ID + ", " +
                        DatabaseConstants.Submissions.COL_PROBLEM_ID + ", " +
                        DatabaseConstants.Submissions.COL_VERDICT_ID + ") VALUES (?, ?, ?)")) {

            ps.setInt(1, 1); // Alice
            ps.setInt(2, 1); // Problem 1
            ps.setInt(3, 1); // Accepted
            ps.executeUpdate();

            ps.setInt(1, 2); // Bob
            ps.setInt(2, 1); // Problem 1
            ps.setInt(3, 1); // Accepted
            ps.executeUpdate();

            ps.setInt(1, 3); // Charlie
            ps.setInt(2, 1); // Problem 1
            ps.setInt(3, 1); // Accepted
            ps.executeUpdate();
        }

        List<UserWithScore> leaderboard = leaderboardDAO.getUsersByScore();

        assertEquals(1, leaderboard.get(0).getScore());
        assertEquals(1, leaderboard.get(1).getScore());
        assertEquals(1, leaderboard.get(2).getScore());

        assertEquals(0, leaderboard.get(3).getScore());
        assertEquals(0, leaderboard.get(4).getScore());
    }

    @Test
    void testGetUsersByScoreLargeDataset() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + DatabaseConstants.Submissions.TABLE_NAME +
                        " (" + DatabaseConstants.Submissions.COL_USER_ID + ", " +
                        DatabaseConstants.Submissions.COL_PROBLEM_ID + ", " +
                        DatabaseConstants.Submissions.COL_VERDICT_ID + ") VALUES (?, ?, ?)")) {

            for (int i = 1; i <= 50; i++) {
                ps.setInt(1, 1);
                ps.setInt(2, i);
                ps.setInt(3, 1);
                ps.executeUpdate();
            }

            for (int i = 1; i <= 100; i++) {
                ps.setInt(1, 2);
                ps.setInt(2, i % 20 + 1);
                ps.setInt(3, i <= 10 ? 1 : 2);
                ps.executeUpdate();
            }
        }

        List<UserWithScore> leaderboard = leaderboardDAO.getUsersByScore();

        UserWithScore alice = leaderboard.stream()
                .filter(u -> u.getUsername().equals("Alice"))
                .findFirst()
                .orElseThrow();
        assertEquals(50, alice.getScore());

        UserWithScore bob = leaderboard.stream()
                .filter(u -> u.getUsername().equals("Bob"))
                .findFirst()
                .orElseThrow();
        assertEquals(10, bob.getScore());
    }

    private void insertSubmissions() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + DatabaseConstants.Submissions.TABLE_NAME +
                        " (" + DatabaseConstants.Submissions.COL_USER_ID + ", " +
                        DatabaseConstants.Submissions.COL_PROBLEM_ID + ", " +
                        DatabaseConstants.Submissions.COL_VERDICT_ID + ") VALUES (?, ?, ?)")) {

            ps.setInt(1, 1); ps.setInt(2, 1); ps.setInt(3, 1); ps.executeUpdate();
            ps.setInt(1, 1); ps.setInt(2, 2); ps.setInt(3, 1); ps.executeUpdate();
            ps.setInt(1, 1); ps.setInt(2, 3); ps.setInt(3, 1); ps.executeUpdate();

            ps.setInt(1, 2); ps.setInt(2, 1); ps.setInt(3, 1); ps.executeUpdate();
            ps.setInt(1, 2); ps.setInt(2, 4); ps.setInt(3, 1); ps.executeUpdate();

            ps.setInt(1, 3); ps.setInt(2, 5); ps.setInt(3, 1); ps.executeUpdate();

            ps.setInt(1, 4); ps.setInt(2, 1); ps.setInt(3, 2); ps.executeUpdate();
        }
    }
}