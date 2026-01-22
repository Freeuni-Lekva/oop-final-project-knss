package com.example.submissions.dao;

import com.example.submissions.DAO.SQLCodeLanguageDAO;
import com.example.submissions.DTO.CodeLanguage;
import com.example.constants.DatabaseConstants;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLCodeLanguageDAOTest {

    private static BasicDataSource dataSource;
    private SQLCodeLanguageDAO codeLanguageDAO;
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
        codeLanguageDAO = new SQLCodeLanguageDAO(dataSource);
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
            stmt.execute("DROP TABLE IF EXISTS " + DatabaseConstants.CodeLanguages.TABLE_NAME);
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
            // Create code_languages table
            stmt.execute("CREATE TABLE " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                    DatabaseConstants.CodeLanguages.COL_ID + " INT PRIMARY KEY, " +
                    DatabaseConstants.CodeLanguages.COL_LANGUAGE + " VARCHAR(100) NOT NULL UNIQUE" +
                    ")");
        }
    }

    private void insertTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                    DatabaseConstants.CodeLanguages.COL_ID + ", " + DatabaseConstants.CodeLanguages.COL_LANGUAGE +
                    ") VALUES (1, 'Java')");
            stmt.execute("INSERT INTO " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                    DatabaseConstants.CodeLanguages.COL_ID + ", " + DatabaseConstants.CodeLanguages.COL_LANGUAGE +
                    ") VALUES (2, 'Python')");
            stmt.execute("INSERT INTO " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                    DatabaseConstants.CodeLanguages.COL_ID + ", " + DatabaseConstants.CodeLanguages.COL_LANGUAGE +
                    ") VALUES (3, 'C++')");
            stmt.execute("INSERT INTO " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                    DatabaseConstants.CodeLanguages.COL_ID + ", " + DatabaseConstants.CodeLanguages.COL_LANGUAGE +
                    ") VALUES (4, 'JavaScript')");
            stmt.execute("INSERT INTO " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                    DatabaseConstants.CodeLanguages.COL_ID + ", " + DatabaseConstants.CodeLanguages.COL_LANGUAGE +
                    ") VALUES (5, 'C#')");
            stmt.execute("INSERT INTO " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                    DatabaseConstants.CodeLanguages.COL_ID + ", " + DatabaseConstants.CodeLanguages.COL_LANGUAGE +
                    ") VALUES (6, 'Go')");
            stmt.execute("INSERT INTO " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                    DatabaseConstants.CodeLanguages.COL_ID + ", " + DatabaseConstants.CodeLanguages.COL_LANGUAGE +
                    ") VALUES (7, 'Rust')");
            stmt.execute("INSERT INTO " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                    DatabaseConstants.CodeLanguages.COL_ID + ", " + DatabaseConstants.CodeLanguages.COL_LANGUAGE +
                    ") VALUES (8, 'Ruby')");
        }
    }

    @Test
    void testGetCodeLanguageByNameJava() {
        CodeLanguage language = codeLanguageDAO.getCodeLanguageByName("Java");

        assertNotNull(language, "Code language should not be null");
        assertEquals(1, language.getId());
        assertEquals("Java", language.getLanguage());
    }

    @Test
    void testGetCodeLanguageByNameMultiple() {
        // Test multiple language names
        CodeLanguage python = codeLanguageDAO.getCodeLanguageByName("Python");
        assertEquals(2, python.getId());
        assertEquals("Python", python.getLanguage());

        CodeLanguage cpp = codeLanguageDAO.getCodeLanguageByName("C++");
        assertEquals(3, cpp.getId());
        assertEquals("C++", cpp.getLanguage());

        CodeLanguage rust = codeLanguageDAO.getCodeLanguageByName("Rust");
        assertEquals(7, rust.getId());
        assertEquals("Rust", rust.getLanguage());
    }

    @Test
    void testGetCodeLanguageByNameCaseSensitive() {
        assertThrows(RuntimeException.class,
                () -> codeLanguageDAO.getCodeLanguageByName("java"), // lowercase
                "Should throw exception when language name case doesn't match");

        assertThrows(RuntimeException.class,
                () -> codeLanguageDAO.getCodeLanguageByName("PYTHON"), // uppercase
                "Should throw exception when language name case doesn't match");
    }

    @Test
    void testGetCodeLanguageByNameNotFound() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> codeLanguageDAO.getCodeLanguageByName("COBOL"),
                "Should throw exception when language name doesn't exist");
    }

    @Test
    void testGetCodeLanguageByNameNull() {
        assertThrows(RuntimeException.class,
                () -> codeLanguageDAO.getCodeLanguageByName(null),
                "Should throw exception when language name is null");
    }

    @Test
    void testGetCodeLanguageByNameEmpty() {
        assertThrows(RuntimeException.class,
                () -> codeLanguageDAO.getCodeLanguageByName(""),
                "Should throw exception when language name is empty");
    }

    @Test
    void testGetCodeLanguageByNameSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLCodeLanguageDAO daoWithClosedDataSource = new SQLCodeLanguageDAO(closedDataSource);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getCodeLanguageByName("Java"),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testGetCodeLanguageByIdOne() {
        CodeLanguage language = codeLanguageDAO.getCodeLanguageById(1);

        assertNotNull(language, "Code language should not be null");
        assertEquals(1, language.getId());
        assertEquals("Java", language.getLanguage());
    }

    @Test
    void testGetCodeLanguageByIdMultiple() {
        for (int id = 1; id <= 8; id++) {
            CodeLanguage language = codeLanguageDAO.getCodeLanguageById(id);
            assertNotNull(language, "Language should not be null for ID " + id);
            assertEquals(id, language.getId(), "ID should match for language " + id);
            assertNotNull(language.getLanguage(), "Language name should not be null for ID " + id);
        }
    }

    @Test
    void testGetCodeLanguageByIdMapping() {
        assertEquals("Java", codeLanguageDAO.getCodeLanguageById(1).getLanguage());
        assertEquals("Python", codeLanguageDAO.getCodeLanguageById(2).getLanguage());
        assertEquals("C++", codeLanguageDAO.getCodeLanguageById(3).getLanguage());
        assertEquals("JavaScript", codeLanguageDAO.getCodeLanguageById(4).getLanguage());
        assertEquals("C#", codeLanguageDAO.getCodeLanguageById(5).getLanguage());
        assertEquals("Go", codeLanguageDAO.getCodeLanguageById(6).getLanguage());
        assertEquals("Rust", codeLanguageDAO.getCodeLanguageById(7).getLanguage());
        assertEquals("Ruby", codeLanguageDAO.getCodeLanguageById(8).getLanguage());
    }

    @Test
    void testGetCodeLanguageByIdNotFound() {
        assertThrows(RuntimeException.class,
                () -> codeLanguageDAO.getCodeLanguageById(999),
                "Should throw exception when language ID doesn't exist");
    }

    @Test
    void testGetCodeLanguageByIdBoundaryValues() {
        assertThrows(RuntimeException.class,
                () -> codeLanguageDAO.getCodeLanguageById(0),
                "Should throw exception for ID 0");

        assertThrows(RuntimeException.class,
                () -> codeLanguageDAO.getCodeLanguageById(-1),
                "Should throw exception for negative ID");

        CodeLanguage maxLanguage = codeLanguageDAO.getCodeLanguageById(8);
        assertNotNull(maxLanguage);
        assertEquals(8, maxLanguage.getId());

        assertThrows(RuntimeException.class,
                () -> codeLanguageDAO.getCodeLanguageById(9),
                "Should throw exception for ID beyond maximum");
    }

    @Test
    void testGetCodeLanguageByIdSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLCodeLanguageDAO daoWithClosedDataSource = new SQLCodeLanguageDAO(closedDataSource);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getCodeLanguageById(1),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testGetCodeLanguages() {
        List<CodeLanguage> languages = codeLanguageDAO.getCodeLanguages();

        assertNotNull(languages, "Language list should not be null");
        assertEquals(8, languages.size(), "Should return 8 languages");

        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("Java")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("Python")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("C++")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("JavaScript")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("C#")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("Go")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("Rust")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("Ruby")));
    }

    @Test
    void testGetCodeLanguagesEmptyTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + DatabaseConstants.CodeLanguages.TABLE_NAME);
        }

        List<CodeLanguage> languages = codeLanguageDAO.getCodeLanguages();

        assertNotNull(languages);
        assertTrue(languages.isEmpty(), "Should return empty list when no languages");
    }

    @Test
    void testGetCodeLanguagesSQLException() throws SQLException {
        BasicDataSource closedDataSource = new BasicDataSource();
        closedDataSource.close();
        SQLCodeLanguageDAO daoWithClosedDataSource = new SQLCodeLanguageDAO(closedDataSource);

        assertThrows(RuntimeException.class,
                () -> daoWithClosedDataSource.getCodeLanguages(),
                "Should throw RuntimeException when connection fails");
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        int threadCount = 20;
        Thread[] threads = new Thread[threadCount];
        boolean[] success = new boolean[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    List<CodeLanguage> languages = codeLanguageDAO.getCodeLanguages();
                    CodeLanguage langById = codeLanguageDAO.getCodeLanguageById((index % 8) + 1);
                    CodeLanguage langByName = codeLanguageDAO.getCodeLanguageByName("Java");
                    success[index] = languages.size() == 8 && langById != null && langByName != null;
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
    void testLanguageNamesWithSpecialCharacters() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                        DatabaseConstants.CodeLanguages.COL_ID + ", " + DatabaseConstants.CodeLanguages.COL_LANGUAGE +
                        ") VALUES (?, ?)")) {
            ps.setInt(1, 10);
            ps.setString(2, "Objective-C");
            ps.executeUpdate();

            ps.setInt(1, 11);
            ps.setString(2, "F#");
            ps.executeUpdate();
        }

        CodeLanguage objC = codeLanguageDAO.getCodeLanguageByName("Objective-C");
        CodeLanguage fSharp = codeLanguageDAO.getCodeLanguageByName("F#");

        assertNotNull(objC);
        assertEquals(10, objC.getId());
        assertEquals("Objective-C", objC.getLanguage());

        assertNotNull(fSharp);
        assertEquals(11, fSharp.getId());
        assertEquals("F#", fSharp.getLanguage());
    }

    @Test
    void testLanguageConsistency() {
        for (int id = 1; id <= 8; id++) {
            CodeLanguage langById = codeLanguageDAO.getCodeLanguageById(id);
            CodeLanguage langByName = codeLanguageDAO.getCodeLanguageByName(langById.getLanguage());

            assertEquals(langById.getId(), langByName.getId(),
                    "Language retrieved by ID and name should have same ID");
            assertEquals(langById.getLanguage(), langByName.getLanguage(),
                    "Language retrieved by ID and name should have same language name");
        }
    }

    @Test
    void testGetCodeLanguagesOrder() {
        List<CodeLanguage> languages = codeLanguageDAO.getCodeLanguages();

        boolean[] idFound = new boolean[9];
        for (CodeLanguage lang : languages) {
            assertTrue(lang.getId() >= 1 && lang.getId() <= 8,
                    "Language ID should be between 1 and 8");
            idFound[lang.getId()] = true;
        }

        for (int i = 1; i <= 8; i++) {
            assertTrue(idFound[i], "Language with ID " + i + " should be present");
        }
    }

    @Test
    void testLanguageWithVersionNumber() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + DatabaseConstants.CodeLanguages.TABLE_NAME + " (" +
                        DatabaseConstants.CodeLanguages.COL_ID + ", " + DatabaseConstants.CodeLanguages.COL_LANGUAGE +
                        ") VALUES (?, ?)")) {
            ps.setInt(1, 20);
            ps.setString(2, "Java 17");
            ps.executeUpdate();
        }

        CodeLanguage java17 = codeLanguageDAO.getCodeLanguageByName("Java 17");
        assertNotNull(java17);
        assertEquals(20, java17.getId());
        assertEquals("Java 17", java17.getLanguage());
    }
}