package com.example.submissions.dao;

import com.example.problems.DTO.Status;
import com.example.submissions.DAO.SQLStatusDAO;
import com.example.constants.DatabaseConstants.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLStatusDAOTest {

    private static BasicDataSource dataSource;
    private SQLStatusDAO statusDAO;
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
        statusDAO = new SQLStatusDAO(dataSource);
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
            stmt.execute("DROP TABLE IF EXISTS " + ProblemStatus.TABLE_NAME);
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
            stmt.execute("CREATE TABLE " + ProblemStatus.TABLE_NAME + " (" +
                    ProblemStatus.COL_ID + " INT PRIMARY KEY, " +
                    ProblemStatus.COL_STATUS + " VARCHAR(50) NOT NULL UNIQUE" +
                    ")");
        }
    }

    private void insertTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert status types
            stmt.execute("INSERT INTO " + ProblemStatus.TABLE_NAME + " (" +
                    ProblemStatus.COL_ID + ", " + ProblemStatus.COL_STATUS +
                    ") VALUES (1, 'Not Started')");
            stmt.execute("INSERT INTO " + ProblemStatus.TABLE_NAME + " (" +
                    ProblemStatus.COL_ID + ", " + ProblemStatus.COL_STATUS +
                    ") VALUES (2, 'In Progress')");
            stmt.execute("INSERT INTO " + ProblemStatus.TABLE_NAME + " (" +
                    ProblemStatus.COL_ID + ", " + ProblemStatus.COL_STATUS +
                    ") VALUES (3, 'Completed')");
            stmt.execute("INSERT INTO " + ProblemStatus.TABLE_NAME + " (" +
                    ProblemStatus.COL_ID + ", " + ProblemStatus.COL_STATUS +
                    ") VALUES (4, 'Failed')");
            stmt.execute("INSERT INTO " + ProblemStatus.TABLE_NAME + " (" +
                    ProblemStatus.COL_ID + ", " + ProblemStatus.COL_STATUS +
                    ") VALUES (5, 'Skipped')");
        }
    }

    @Test
    void testGetStatuses() {
        List<Status> statuses = statusDAO.getStatuses();

        assertNotNull(statuses, "Status list should not be null");
        assertEquals(5, statuses.size(), "Should return 5 statuses");

        assertTrue(statuses.stream().anyMatch(s -> s.getStatus().equals("Not Started")));
        assertTrue(statuses.stream().anyMatch(s -> s.getStatus().equals("In Progress")));
        assertTrue(statuses.stream().anyMatch(s -> s.getStatus().equals("Completed")));
        assertTrue(statuses.stream().anyMatch(s -> s.getStatus().equals("Failed")));
        assertTrue(statuses.stream().anyMatch(s -> s.getStatus().equals("Skipped")));
    }

    @Test
    void testGetStatusesEmptyTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + ProblemStatus.TABLE_NAME);
        }

        List<Status> statuses = statusDAO.getStatuses();

        assertNotNull(statuses);
        assertTrue(statuses.isEmpty(), "Should return empty list when no statuses");
    }

    @Test
    void testGetStatusesSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLStatusDAO daoWithClosedDataSource = new SQLStatusDAO(closedDataSource);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getStatuses(),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testGetStatusById() {

        Status status = statusDAO.getStatusById(1);

        assertNotNull(status, "Status should not be null");
        assertEquals(1, status.getId());
        assertEquals("Not Started", status.getStatus());
    }

    @Test
    void testGetStatusByIdMultiple() {
        Status status1 = statusDAO.getStatusById(1);
        Status status2 = statusDAO.getStatusById(2);
        Status status3 = statusDAO.getStatusById(3);

        assertEquals("Not Started", status1.getStatus());
        assertEquals("In Progress", status2.getStatus());
        assertEquals("Completed", status3.getStatus());
    }

    @Test
    void testGetStatusByIdNotFound() {
        assertThrows(RuntimeException.class,
                () -> statusDAO.getStatusById(999),
                "Should throw exception when status ID doesn't exist");
    }

    @Test
    void testGetStatusByIdSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLStatusDAO daoWithClosedDataSource = new SQLStatusDAO(closedDataSource);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getStatusById(1),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testGetStatusByStatusName() {
        Status status = statusDAO.getStatusByStatusName("In Progress");

        assertNotNull(status, "Status should not be null");
        assertEquals(2, status.getId());
        assertEquals("In Progress", status.getStatus());
    }

    @Test
    void testGetStatusByStatusNameMultiple() {
        Status status1 = statusDAO.getStatusByStatusName("Not Started");
        Status status2 = statusDAO.getStatusByStatusName("Completed");
        Status status3 = statusDAO.getStatusByStatusName("Failed");

        assertEquals(1, status1.getId());
        assertEquals(3, status2.getId());
        assertEquals(4, status3.getId());
    }

    @Test
    void testGetStatusByStatusNameCaseSensitive() {
        assertThrows(RuntimeException.class,
                () -> statusDAO.getStatusByStatusName("not started"), // lowercase
                "Should throw exception when status name case doesn't match");
    }

    @Test
    void testGetStatusByStatusNameNotFound() {
        assertThrows(RuntimeException.class,
                () -> statusDAO.getStatusByStatusName("Non Existent Status"),
                "Should throw exception when status name doesn't exist");
    }

    @Test
    void testGetStatusByStatusNameSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLStatusDAO daoWithClosedDataSource = new SQLStatusDAO(closedDataSource);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getStatusByStatusName("Completed"),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testGetStatusByStatusNameWithSpecialChars() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + ProblemStatus.TABLE_NAME + " (" +
                        ProblemStatus.COL_ID + ", " + ProblemStatus.COL_STATUS + ") VALUES (?, ?)")) {
            ps.setInt(1, 10);
            ps.setString(2, "Status-With-Dashes");
            ps.executeUpdate();
        }

        Status status = statusDAO.getStatusByStatusName("Status-With-Dashes");

        assertNotNull(status);
        assertEquals(10, status.getId());
        assertEquals("Status-With-Dashes", status.getStatus());
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        boolean[] success = new boolean[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    List<Status> statuses = statusDAO.getStatuses();
                    success[index] = statuses.size() == 5;
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
    void testGetStatusByStatusNameNull() {
        assertThrows(RuntimeException.class,
                () -> statusDAO.getStatusByStatusName(null),
                "Should throw exception when status name is null");
    }

    @Test
    void testGetStatusByIdBoundaryValues() {
        // Test minimum ID
        Status minStatus = statusDAO.getStatusById(1);
        assertNotNull(minStatus);
        assertEquals(1, minStatus.getId());

        // Test maximum ID in our test data
        Status maxStatus = statusDAO.getStatusById(5);
        assertNotNull(maxStatus);
        assertEquals(5, maxStatus.getId());

        // Test ID 0 (assuming it doesn't exist)
        assertThrows(RuntimeException.class,
                () -> statusDAO.getStatusById(0));

        // Test negative ID
        assertThrows(RuntimeException.class,
                () -> statusDAO.getStatusById(-1));
    }
}