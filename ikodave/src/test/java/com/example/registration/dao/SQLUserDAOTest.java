package com.example.registration.dao;

import com.example.registration.DTO.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {

    private static BasicDataSource dataSource;
    private static SQLUserDAO userDao;

    @BeforeAll
    static void setupDatabase() throws Exception {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");

        userDao = new SQLUserDAO(dataSource);

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    role_id INT DEFAULT 2,
                    register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
        }
    }

    @AfterEach
    void clearData() throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
        }
    }

    @Test
    void testAddAndGetUserById() {
        User user = new User("kende", "1234");
        userDao.addUser(user);
        assertTrue(userDao.usernameExists("kende"));

        User retrieved = userDao.getUserByUsername("kende");
        assertNotNull(retrieved);
        assertEquals("kende", retrieved.getUsername());
        assertEquals("1234", retrieved.getPasswordHash());
    }

    @Test
    void testAuthenticate() {
        User user = new User("nmetr", "pass");
        userDao.addUser(user);

        assertTrue(userDao.authenticate(new User("nmetr", "pass")));
        assertFalse(userDao.authenticate(new User("nmetr", "wrong")));
        assertFalse(userDao.authenticate(new User("nosuchuser", "pass")));
    }

    @Test
    void testDeleteUser() {
        User user = new User("slosa", "pwd");
        userDao.addUser(user);
        assertTrue(userDao.usernameExists("slosa"));

        userDao.deleteUser("slosa");
        assertFalse(userDao.usernameExists("slosa"));
    }

    @Test
    void testGetUserByIdByUsernameReturnsNullIfNotFound() {
        assertNull(userDao.getUserByUsername("notfound"));
    }

    @Test
    void testAddUserWithSpecialCharacters() {
        User user = new User("usér@#%$", "päss@!$%");
        userDao.addUser(user);
        assertTrue(userDao.usernameExists("usér@#%$"));
    }

    @Test
    void testAddUserWithLongUsernameAndPassword() {
        String longUsername = "u".repeat(255);
        String longPassword = "p".repeat(255);
        User user = new User(longUsername, longPassword);
        userDao.addUser(user);

        assertTrue(userDao.usernameExists(longUsername));
        User retrieved = userDao.getUserByUsername(longUsername);
        assertEquals(longPassword, retrieved.getPasswordHash());
    }

    @Test
    void testAddDuplicateUsernameFails() {
        User user1 = new User("duplicate", "pass1");
        User user2 = new User("duplicate", "pass2");

        userDao.addUser(user1);
        assertThrows(RuntimeException.class, () -> userDao.addUser(user2));
    }

    @Test
    void testGetUserByIdById() {
        User user = new User("byId", "1234");
        userDao.addUser(user);

        User retrieved = userDao.getUserByUsername("byId");
        assertNotNull(retrieved);

        User byId = userDao.getUserById(retrieved.getId());
        assertEquals("byId", byId.getUsername());
    }

    @Test
    void testDeleteNonExistentUser() {
        assertDoesNotThrow(() -> userDao.deleteUser("nope"));
    }

    @Test
    void testRegisterDateIsSetAutomatically() {
        User user = new User("datecheck", "pwd");
        userDao.addUser(user);
        User retrieved = userDao.getUserByUsername("datecheck");

        assertNotNull(retrieved.getRegisterDate());
    }

    @Test
    void testSQLInjectionPrevention() {
        User injection = new User("'; DROP TABLE users; --", "hack");
        assertDoesNotThrow(() -> userDao.addUser(injection));
        assertTrue(userDao.usernameExists("'; DROP TABLE users; --"));
    }

    @Test
    void testMassInsertionAndRetrieval() {
        for (int i = 0; i < 50; i++) {
            userDao.addUser(new User("user" + i, "pass" + i));
        }

        for (int i = 0; i < 50; i++) {
            assertTrue(userDao.usernameExists("user" + i));
            User retrieved = userDao.getUserByUsername("user" + i);
            assertEquals("pass" + i, retrieved.getPasswordHash());
        }
    }

    @Test
    void testAuthenticateAfterDeletionFails() {
        User user = new User("tempuser", "temppass");
        userDao.addUser(user);
        assertTrue(userDao.authenticate(user));

        userDao.deleteUser("tempuser");
        assertFalse(userDao.authenticate(user));
    }

    @Test
    void testCaseSensitivity() {
        userDao.addUser(new User("CaseUser", "CasePass"));
        assertFalse(userDao.usernameExists("caseuser"));
        assertFalse(userDao.authenticate(new User("caseuser", "CasePass")));
    }
}
