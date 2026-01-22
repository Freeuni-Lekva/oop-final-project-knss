package com.example.submissions.dao;

import com.example.submissions.DAO.SQLSubmissionDAO;
import com.example.submissions.DAO.ToSQL;
import com.example.submissions.DTO.Submission;
import com.example.constants.DatabaseConstants.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SQLSubmissionDAOTest {

    private static BasicDataSource dataSource;
    private SQLSubmissionDAO submissionDAO;
    private Connection connection;

    @BeforeAll
    static void setUpDatabase() {
        // Create H2 in-memory database
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
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
        submissionDAO = new SQLSubmissionDAO(dataSource);
        connection = dataSource.getConnection();

        // Create tables
        createTables();

        // Insert test data
        insertTestData();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Drop all tables
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS " + Submissions.TABLE_NAME);
            stmt.execute("DROP TABLE IF EXISTS " + SubmissionVerdict.TABLE_NAME);
            stmt.execute("DROP TABLE IF EXISTS " + CodeLanguages.TABLE_NAME);
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
            // Create submission_verdict table
            stmt.execute("CREATE TABLE " + SubmissionVerdict.TABLE_NAME + " (" +
                    SubmissionVerdict.COL_ID + " INT PRIMARY KEY, " +
                    SubmissionVerdict.COL_VERDICT + " VARCHAR(50) NOT NULL" +
                    ")");

            // Create code_languages table
            stmt.execute("CREATE TABLE " + CodeLanguages.TABLE_NAME + " (" +
                    CodeLanguages.COL_ID + " INT PRIMARY KEY, " +
                    CodeLanguages.COL_LANGUAGE + " VARCHAR(50) NOT NULL" +
                    ")");

            // Create submissions table - using the actual column names from DatabaseConstants
            stmt.execute("CREATE TABLE " + Submissions.TABLE_NAME + " (" +
                    Submissions.COL_ID + " INT PRIMARY KEY AUTO_INCREMENT, " +
                    Submissions.COL_USER_ID + " INT NOT NULL, " +
                    Submissions.COL_PROBLEM_ID + " INT NOT NULL, " +
                    Submissions.COL_VERDICT_ID + " INT NOT NULL, " +
                    Submissions.COL_SOLUTION + " VARCHAR(4000), " +
                    Submissions.COL_CODE_LANGUAGE_ID + " INT NOT NULL, " +
                    Submissions.COL_TIME + " BIGINT, " +
                    Submissions.COL_MEMORY + " BIGINT, " +
                    Submissions.COL_SUBMIT_DATE + " TIMESTAMP, " +
                    Submissions.COL_LOG + " VARCHAR(1000), " +
                    "FOREIGN KEY (" + Submissions.COL_VERDICT_ID + ") REFERENCES " + SubmissionVerdict.TABLE_NAME + "(" + SubmissionVerdict.COL_ID + "), " +
                    "FOREIGN KEY (" + Submissions.COL_CODE_LANGUAGE_ID + ") REFERENCES " + CodeLanguages.TABLE_NAME + "(" + CodeLanguages.COL_ID + ")" +
                    ")");
        }
    }

    private void insertTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert verdicts
            stmt.execute("INSERT INTO " + SubmissionVerdict.TABLE_NAME + " (" + SubmissionVerdict.COL_ID + ", " + SubmissionVerdict.COL_VERDICT + ") VALUES (1, 'Accepted')");
            stmt.execute("INSERT INTO " + SubmissionVerdict.TABLE_NAME + " (" + SubmissionVerdict.COL_ID + ", " + SubmissionVerdict.COL_VERDICT + ") VALUES (2, 'Wrong Answer')");
            stmt.execute("INSERT INTO " + SubmissionVerdict.TABLE_NAME + " (" + SubmissionVerdict.COL_ID + ", " + SubmissionVerdict.COL_VERDICT + ") VALUES (3, 'Time Limit Exceeded')");
            stmt.execute("INSERT INTO " + SubmissionVerdict.TABLE_NAME + " (" + SubmissionVerdict.COL_ID + ", " + SubmissionVerdict.COL_VERDICT + ") VALUES (4, 'Runtime Error')");
            stmt.execute("INSERT INTO " + SubmissionVerdict.TABLE_NAME + " (" + SubmissionVerdict.COL_ID + ", " + SubmissionVerdict.COL_VERDICT + ") VALUES (5, 'Compilation Error')");

            // Insert code languages
            stmt.execute("INSERT INTO " + CodeLanguages.TABLE_NAME + " (" + CodeLanguages.COL_ID + ", " + CodeLanguages.COL_LANGUAGE + ") VALUES (1, 'Java')");
            stmt.execute("INSERT INTO " + CodeLanguages.TABLE_NAME + " (" + CodeLanguages.COL_ID + ", " + CodeLanguages.COL_LANGUAGE + ") VALUES (2, 'Python')");
            stmt.execute("INSERT INTO " + CodeLanguages.TABLE_NAME + " (" + CodeLanguages.COL_ID + ", " + CodeLanguages.COL_LANGUAGE + ") VALUES (3, 'C++')");
        }
    }

    @Test
    void testInsertSubmission() {
        Submission submission = createTestSubmission();

        int generatedId = submissionDAO.insertSubmission(submission);

        assertTrue(generatedId > 0, "Generated ID should be positive");

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM " + Submissions.TABLE_NAME + " WHERE " + Submissions.COL_ID + " = ?")) {
            ps.setInt(1, generatedId);
            ResultSet rs = ps.executeQuery();

            assertTrue(rs.next(), "Submission should exist in database");
            assertEquals(submission.getUserId(), rs.getInt(Submissions.COL_USER_ID));
            assertEquals(submission.getProblemId(), rs.getInt(Submissions.COL_PROBLEM_ID));
            assertEquals(submission.getVerdictId(), rs.getInt(Submissions.COL_VERDICT_ID));
            assertEquals(submission.getSolutionCode(), rs.getString(Submissions.COL_SOLUTION));
            assertEquals(submission.getCodeLanguageId(), rs.getInt(Submissions.COL_CODE_LANGUAGE_ID));
            assertEquals(submission.getTime(), rs.getLong(Submissions.COL_TIME));
            assertEquals(submission.getMemory(), rs.getLong(Submissions.COL_MEMORY));
            assertEquals(submission.getLog(), rs.getString(Submissions.COL_LOG));
        } catch (SQLException e) {
            fail("Failed to verify insertion: " + e.getMessage());
        }
    }

    @Test
    void testInsertSubmissionSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLSubmissionDAO daoWithClosedDataSource = new SQLSubmissionDAO(closedDataSource);

        Submission submission = createTestSubmission();


        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.insertSubmission(submission),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testUpdateSubmission() throws SQLException {
        // Arrange - Insert initial submission
        Submission submission = createTestSubmission();
        int id = submissionDAO.insertSubmission(submission);
        submission.setId(id);

        // Modify submission
        submission.setVerdictId(2); // Change to "Wrong Answer"
        submission.setSolutionCode("Updated solution code");
        submission.setTime(2000L);
        submission.setMemory(512L);
        submission.setLog("Updated log");

        // Act
        submissionDAO.updateSubmission(submission);

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM " + Submissions.TABLE_NAME + " WHERE " + Submissions.COL_ID + " = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            assertTrue(rs.next(), "Submission should exist");
            assertEquals(2, rs.getInt(Submissions.COL_VERDICT_ID));
            assertEquals("Updated solution code", rs.getString(Submissions.COL_SOLUTION));
            assertEquals(2000L, rs.getLong(Submissions.COL_TIME));
            assertEquals(512L, rs.getLong(Submissions.COL_MEMORY));
            assertEquals("Updated log", rs.getString(Submissions.COL_LOG));
        }
    }

    @Test
    void testUpdateSubmissionSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLSubmissionDAO daoWithClosedDataSource = new SQLSubmissionDAO(closedDataSource);

        Submission submission = createTestSubmission();
        submission.setId(1);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.updateSubmission(submission),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testGetSubmissionsBy() {
        insertMultipleSubmissions();

        List<Submission> submissions = submissionDAO.getSubmissionsBy(1, 100);

        assertEquals(3, submissions.size(), "Should return 3 submissions for user 1, problem 100");


        for (Submission submission : submissions) {
            assertEquals(1, submission.getUserId());
            assertEquals(100, submission.getProblemId());
        }
    }

    @Test
    void testGetSubmissionsByNoResults() {
        List<Submission> submissions = submissionDAO.getSubmissionsBy(999, 999);

        assertNotNull(submissions);
        assertTrue(submissions.isEmpty(), "Should return empty list when no submissions found");
    }

    @Test
    void testGetSubmissionsBySQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLSubmissionDAO daoWithClosedDataSource = new SQLSubmissionDAO(closedDataSource);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getSubmissionsBy(1, 100),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testGetSubmissionsByOrder() throws InterruptedException {
        Timestamp earliest = new Timestamp(System.currentTimeMillis() - 3000);
        Timestamp middle = new Timestamp(System.currentTimeMillis() - 2000);
        Timestamp latest = new Timestamp(System.currentTimeMillis() - 1000);

        insertSubmissionWithTimestamp(1, 100, earliest);
        insertSubmissionWithTimestamp(1, 100, middle);
        insertSubmissionWithTimestamp(1, 100, latest);

        List<Submission> submissions = submissionDAO.getSubmissionsByOrder(1, 100);

        assertEquals(3, submissions.size());

        assertTrue(submissions.get(0).getSubmitDate().getTime() >= submissions.get(1).getSubmitDate().getTime());
        assertTrue(submissions.get(1).getSubmitDate().getTime() >= submissions.get(2).getSubmitDate().getTime());
    }

    @Test
    void testGetSubmissionsByOrderSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLSubmissionDAO daoWithClosedDataSource = new SQLSubmissionDAO(closedDataSource);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getSubmissionsByOrder(1, 100),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testInsertSubmissionWithNulls() {
        Submission submission = new Submission();
        submission.setUserId(1);
        submission.setProblemId(100);
        submission.setVerdictId(1);
        submission.setSolutionCode(null);
        submission.setCodeLanguageId(1);
        submission.setTime(0);
        submission.setMemory(0);
        submission.setSubmitDate(new Timestamp(System.currentTimeMillis()));
        submission.setLog(null);

        int generatedId = submissionDAO.insertSubmission(submission);

        assertTrue(generatedId > 0);

        List<Submission> retrieved = submissionDAO.getSubmissionsBy(1, 100);
        assertEquals(1, retrieved.size());
        assertNull(retrieved.get(0).getSolutionCode());
        assertNull(retrieved.get(0).getLog());
    }

    @Test
    void debugPrintSQLStatements() {
        System.out.println("Insert SQL: " + ToSQL.toInsertSubmissionNoIdSQL());
        System.out.println("Update SQL: " + ToSQL.toUpdateSubmissionSQL());
        System.out.println("Select SQL: " + ToSQL.toSubmissionSQL());
        System.out.println("Select Sorted SQL: " + ToSQL.toSubmissionSortedSQL());
    }


    private Submission createTestSubmission() {
        Submission submission = new Submission();
        submission.setUserId(1);
        submission.setProblemId(100);
        submission.setVerdictId(1); // Accepted
        submission.setSolutionCode("public class Solution { }");
        submission.setCodeLanguageId(1); // Java
        submission.setTime(1000L);
        submission.setMemory(256L);
        submission.setSubmitDate(new Timestamp(System.currentTimeMillis()));
        submission.setLog("Test log");
        return submission;
    }

    private void insertMultipleSubmissions() {
        for (int i = 0; i < 3; i++) {
            Submission submission = createTestSubmission();
            submission.setVerdictId(i % 2 == 0 ? 1 : 2);
            submissionDAO.insertSubmission(submission);
        }

        // Insert submissions for different users/problems
        Submission otherUserSubmission = createTestSubmission();
        otherUserSubmission.setUserId(2);
        submissionDAO.insertSubmission(otherUserSubmission);

        Submission otherProblemSubmission = createTestSubmission();
        otherProblemSubmission.setProblemId(200);
        submissionDAO.insertSubmission(otherProblemSubmission);
    }

    private void insertSubmissionWithTimestamp(int userId, int problemId, Timestamp timestamp) {
        Submission submission = createTestSubmission();
        submission.setUserId(userId);
        submission.setProblemId(problemId);
        submission.setSubmitDate(timestamp);
        submissionDAO.insertSubmission(submission);
    }
}