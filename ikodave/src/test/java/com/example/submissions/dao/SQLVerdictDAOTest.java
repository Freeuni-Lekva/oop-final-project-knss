package com.example.submissions.dao;

import com.example.submissions.DAO.SQLVerdictDAO;
import com.example.submissions.DTO.SubmissionVerdict;
import com.example.constants.DatabaseConstants;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLVerdictDAOTest {

    private static BasicDataSource dataSource;
    private SQLVerdictDAO verdictDAO;
    private Connection connection;

    @BeforeAll
    static void setUpDatabase() {
        // Create H2 in-memory database
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        dataSource.setInitialSize(5);
        dataSource.setMaxTotal(10);
        dataSource.setMaxIdle(5);
        dataSource.setMinIdle(2);
    }

    @BeforeEach
    void setUp() throws SQLException {
        verdictDAO = new SQLVerdictDAO(dataSource);
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
            // Create submission_verdict table
            stmt.execute("CREATE TABLE " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                    DatabaseConstants.SubmissionVerdict.COL_ID + " INT PRIMARY KEY, " +
                    DatabaseConstants.SubmissionVerdict.COL_VERDICT + " VARCHAR(100) NOT NULL UNIQUE" +
                    ")");
        }
    }

    private void insertTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert verdict types
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                    DatabaseConstants.SubmissionVerdict.COL_ID + ", " + DatabaseConstants.SubmissionVerdict.COL_VERDICT +
                    ") VALUES (1, 'Accepted')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                    DatabaseConstants.SubmissionVerdict.COL_ID + ", " + DatabaseConstants.SubmissionVerdict.COL_VERDICT +
                    ") VALUES (2, 'Wrong Answer')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                    DatabaseConstants.SubmissionVerdict.COL_ID + ", " + DatabaseConstants.SubmissionVerdict.COL_VERDICT +
                    ") VALUES (3, 'Time Limit Exceeded')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                    DatabaseConstants.SubmissionVerdict.COL_ID + ", " + DatabaseConstants.SubmissionVerdict.COL_VERDICT +
                    ") VALUES (4, 'Memory Limit Exceeded')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                    DatabaseConstants.SubmissionVerdict.COL_ID + ", " + DatabaseConstants.SubmissionVerdict.COL_VERDICT +
                    ") VALUES (5, 'Runtime Error')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                    DatabaseConstants.SubmissionVerdict.COL_ID + ", " + DatabaseConstants.SubmissionVerdict.COL_VERDICT +
                    ") VALUES (6, 'Compilation Error')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                    DatabaseConstants.SubmissionVerdict.COL_ID + ", " + DatabaseConstants.SubmissionVerdict.COL_VERDICT +
                    ") VALUES (7, 'Output Limit Exceeded')");
            stmt.execute("INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                    DatabaseConstants.SubmissionVerdict.COL_ID + ", " + DatabaseConstants.SubmissionVerdict.COL_VERDICT +
                    ") VALUES (8, 'Presentation Error')");
        }
    }

    @Test
    void testGetVerdictByNameAccepted() {
        SubmissionVerdict verdict = verdictDAO.getVerdictByName("Accepted");

        assertNotNull(verdict, "Verdict should not be null");
        assertEquals(1, verdict.getId());
        assertEquals("Accepted", verdict.getVerdict());
    }

    @Test
    void testGetVerdictByNameMultiple() {
        SubmissionVerdict wrongAnswer = verdictDAO.getVerdictByName("Wrong Answer");
        assertEquals(2, wrongAnswer.getId());
        assertEquals("Wrong Answer", wrongAnswer.getVerdict());

        SubmissionVerdict timeLimitExceeded = verdictDAO.getVerdictByName("Time Limit Exceeded");
        assertEquals(3, timeLimitExceeded.getId());
        assertEquals("Time Limit Exceeded", timeLimitExceeded.getVerdict());

        SubmissionVerdict runtimeError = verdictDAO.getVerdictByName("Runtime Error");
        assertEquals(5, runtimeError.getId());
        assertEquals("Runtime Error", runtimeError.getVerdict());
    }

    @Test
    void testGetVerdictByNameCaseSensitive() {
        assertThrows(RuntimeException.class,
                () -> verdictDAO.getVerdictByName("accepted"), // lowercase
                "Should throw exception when verdict name case doesn't match");

        assertThrows(RuntimeException.class,
                () -> verdictDAO.getVerdictByName("ACCEPTED"), // uppercase
                "Should throw exception when verdict name case doesn't match");
    }

    @Test
    void testGetVerdictByNameNotFound() {
        assertThrows(RuntimeException.class,
                () -> verdictDAO.getVerdictByName("Non Existent Verdict"),
                "Should throw exception when verdict name doesn't exist");
    }

    @Test
    void testGetVerdictByNameNull() {
        assertThrows(RuntimeException.class,
                () -> verdictDAO.getVerdictByName(null),
                "Should throw exception when verdict name is null");
    }

    @Test
    void testGetVerdictByNameEmpty() {
        assertThrows(RuntimeException.class,
                () -> verdictDAO.getVerdictByName(""),
                "Should throw exception when verdict name is empty");
    }

    @Test
    void testGetVerdictByNameSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLVerdictDAO daoWithClosedDataSource = new SQLVerdictDAO(closedDataSource);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getVerdictByName("Accepted"),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testGetVerdictByIdOne() {
        SubmissionVerdict verdict = verdictDAO.getVerdictById(1);

        assertNotNull(verdict, "Verdict should not be null");
        assertEquals(1, verdict.getId());
        assertEquals("Accepted", verdict.getVerdict());
    }

    @Test
    void testGetVerdictByIdMultiple() {
        for (int id = 1; id <= 8; id++) {
            SubmissionVerdict verdict = verdictDAO.getVerdictById(id);
            assertNotNull(verdict, "Verdict should not be null for ID " + id);
            assertEquals(id, verdict.getId(), "ID should match for verdict " + id);
            assertNotNull(verdict.getVerdict(), "Verdict name should not be null for ID " + id);
        }
    }

    @Test
    void testGetVerdictByIdMapping() {
        assertEquals("Accepted", verdictDAO.getVerdictById(1).getVerdict());
        assertEquals("Wrong Answer", verdictDAO.getVerdictById(2).getVerdict());
        assertEquals("Time Limit Exceeded", verdictDAO.getVerdictById(3).getVerdict());
        assertEquals("Memory Limit Exceeded", verdictDAO.getVerdictById(4).getVerdict());
        assertEquals("Runtime Error", verdictDAO.getVerdictById(5).getVerdict());
        assertEquals("Compilation Error", verdictDAO.getVerdictById(6).getVerdict());
        assertEquals("Output Limit Exceeded", verdictDAO.getVerdictById(7).getVerdict());
        assertEquals("Presentation Error", verdictDAO.getVerdictById(8).getVerdict());
    }

    @Test
    void testGetVerdictByIdNotFound() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> verdictDAO.getVerdictById(999),
                "Should throw exception when verdict ID doesn't exist");
    }

    @Test
    void testGetVerdictByIdBoundaryValues() {
        assertThrows(RuntimeException.class,
                () -> verdictDAO.getVerdictById(0),
                "Should throw exception for ID 0");

        assertThrows(RuntimeException.class,
                () -> verdictDAO.getVerdictById(-1),
                "Should throw exception for negative ID");

        SubmissionVerdict maxVerdict = verdictDAO.getVerdictById(8);
        assertNotNull(maxVerdict);
        assertEquals(8, maxVerdict.getId());

        assertThrows(RuntimeException.class,
                () -> verdictDAO.getVerdictById(9),
                "Should throw exception for ID beyond maximum");
    }

    @Test
    void testGetVerdictByIdSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLVerdictDAO daoWithClosedDataSource = new SQLVerdictDAO(closedDataSource);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getVerdictById(1),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        int threadCount = 20;
        Thread[] threads = new Thread[threadCount];
        boolean[] success = new boolean[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            final int verdictId = (i % 8) + 1;
            threads[i] = new Thread(() -> {
                try {
                    SubmissionVerdict verdict = verdictDAO.getVerdictById(verdictId);
                    success[index] = verdict != null && verdict.getId() == verdictId;
                } catch (Exception e) {
                    success[index] = false;
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        for (int i = 0; i < threadCount; i++) {
            assertTrue(success[i], "Thread " + i + " should have succeeded");
        }
    }

    @Test
    void testVerdictNamesWithSpecialCharacters() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                        DatabaseConstants.SubmissionVerdict.COL_ID + ", " + DatabaseConstants.SubmissionVerdict.COL_VERDICT +
                        ") VALUES (?, ?)")) {
            ps.setInt(1, 10);
            ps.setString(2, "Verdict-With-Special_Characters!");
            ps.executeUpdate();
        }

        SubmissionVerdict verdict = verdictDAO.getVerdictByName("Verdict-With-Special_Characters!");

        assertNotNull(verdict);
        assertEquals(10, verdict.getId());
        assertEquals("Verdict-With-Special_Characters!", verdict.getVerdict());
    }

    @Test
    void testVerdictConsistency() {
        for (int id = 1; id <= 8; id++) {
            SubmissionVerdict verdictById = verdictDAO.getVerdictById(id);
            SubmissionVerdict verdictByName = verdictDAO.getVerdictByName(verdictById.getVerdict());

            assertEquals(verdictById.getId(), verdictByName.getId(),
                    "Verdict retrieved by ID and name should have same ID");
            assertEquals(verdictById.getVerdict(), verdictByName.getVerdict(),
                    "Verdict retrieved by ID and name should have same verdict name");
        }
    }

    @Test
    void testVeryLongVerdictName() throws SQLException {
        String longVerdictName = "Very_Long_Verdict_Name_" + "x".repeat(70);

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + DatabaseConstants.SubmissionVerdict.TABLE_NAME + " (" +
                        DatabaseConstants.SubmissionVerdict.COL_ID + ", " + DatabaseConstants.SubmissionVerdict.COL_VERDICT +
                        ") VALUES (?, ?)")) {
            ps.setInt(1, 20);
            ps.setString(2, longVerdictName);
            ps.executeUpdate();
        }

        SubmissionVerdict verdict = verdictDAO.getVerdictByName(longVerdictName);

        assertNotNull(verdict);
        assertEquals(20, verdict.getId());
        assertEquals(longVerdictName, verdict.getVerdict());
    }
}