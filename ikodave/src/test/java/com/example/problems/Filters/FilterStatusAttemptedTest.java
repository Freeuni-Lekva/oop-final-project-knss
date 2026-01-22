package com.example.problems.Filters;

import com.example.problems.Filters.Parameters.Parameter;
import com.example.registration.DTO.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilterStatusAttemptedTest {

    @Test
    void testToSQLStatement_GeneratesExpectedSQL() {
        User user = new User();
        user.setId(42);

        FilterStatusAttempted filter = new FilterStatusAttempted(user);

        String sql = filter.toSQLStatement();

        assertNotNull(sql);
        assertTrue(sql.contains("SELECT"));
        assertTrue(sql.contains("WHERE"));
        assertTrue(sql.contains("?"));
    }

    @Test
    void testGetParameters_ReturnsExpectedParameters() {
        User user = new User();
        user.setId(123);

        FilterStatusAttempted filter = new FilterStatusAttempted(user);

        List<Parameter> parameters = filter.getParameters();

        assertEquals(2, parameters.size());
    }

    @Test
    void testToSQLPreparedStatement_SetsParametersCorrectly() throws SQLException {
        User user = new User();
        user.setId(100);

        FilterStatusAttempted filter = new FilterStatusAttempted(user);

        // Mock connection and prepared statement
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        PreparedStatement ps = filter.toSQLPreparedStatement(mockConnection);

        assertNotNull(ps);

        // Verify that setLong/setString were called with expected values
        // (your Parameter classes will call setInt/setString internally)
        verify(mockPreparedStatement, atLeastOnce()).setInt(eq(1), eq(100)); // User ID
        verify(mockPreparedStatement, atLeastOnce()).setString(eq(2), eq("Accepted"));
    }
}
