package com.example.admin.dao;

import com.example.admin.dto.Role;
import com.example.constants.DatabaseConstants;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class SQLRoleDAOTest {

    private static BasicDataSource dataSource;
    private static SQLRoleDAO roleDAO;

    @BeforeAll
    static void setupDatabase() throws Exception {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");

        roleDAO = new SQLRoleDAO(dataSource);

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(String.format("""
                CREATE TABLE %s (
                    %s INT PRIMARY KEY,
                    %s VARCHAR(255) NOT NULL
                )
            """,
                    DatabaseConstants.UserRole.TABLE_NAME,
                    DatabaseConstants.UserRole.COL_ID,
                    DatabaseConstants.UserRole.COL_ROLE
            ));
        }
    }

    @AfterEach
    void clearData() throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(String.format("DELETE FROM %s", DatabaseConstants.UserRole.TABLE_NAME));
        }
    }

    @Test
    void testGetRoleByIdReturnsRole() throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(String.format(
                    "INSERT INTO %s (%s, %s) VALUES (1, 'Admin')",
                    DatabaseConstants.UserRole.TABLE_NAME,
                    DatabaseConstants.UserRole.COL_ID,
                    DatabaseConstants.UserRole.COL_ROLE
            ));
        }

        Role role = roleDAO.getRoleById(1);
        assertNotNull(role);
        assertEquals(1, role.getId());
        assertEquals("Admin", role.getRole());
    }

    @Test
    void testGetRoleByIdReturnsNullIfNotFound() {
        Role role = roleDAO.getRoleById(999);
        assertNull(role);
    }

    @Test
    void testMultipleRoles() throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(String.format(
                    "INSERT INTO %s (%s, %s) VALUES (1, 'Admin'), (2, 'User'), (3, 'Moderator')",
                    DatabaseConstants.UserRole.TABLE_NAME,
                    DatabaseConstants.UserRole.COL_ID,
                    DatabaseConstants.UserRole.COL_ROLE
            ));
        }

        Role role1 = roleDAO.getRoleById(1);
        Role role2 = roleDAO.getRoleById(2);
        Role role3 = roleDAO.getRoleById(3);

        assertEquals("Admin", role1.getRole());
        assertEquals("User", role2.getRole());
        assertEquals("Moderator", role3.getRole());
    }

    @Test
    void testGetRoleByIdHandlesInvalidId() {
        assertNull(roleDAO.getRoleById(-1));
    }
}
