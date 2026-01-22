package com.example.problems.Filters;

import com.example.problems.Filters.Parameters.Parameter;
import com.example.constants.DatabaseConstants.Problems;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilterAndLoggedInTest {

    @Test
    void testToSQLStatement_WithMultipleFilters() {
        Filter f1 = mock(Filter.class);
        Filter f2 = mock(Filter.class);

        when(f1.toSQLStatement()).thenReturn("SELECT id, 'status1' AS status FROM problems WHERE id > 10");
        when(f2.toSQLStatement()).thenReturn("SELECT id, 'status2' AS status FROM problems WHERE id < 100");

        FilterAndLoggedIn filter = new FilterAndLoggedIn();
        filter.addFilter(f1);
        filter.addFilter(f2);

        String sql = filter.toSQLStatement();

        assertTrue(sql.startsWith("WITH "));
        assertTrue(sql.contains("t0 AS (SELECT id, 'status1' AS status FROM problems WHERE id > 10)"));
        assertTrue(sql.contains("t1 AS (SELECT id, 'status2' AS status FROM problems WHERE id < 100)"));
        assertTrue(sql.contains("JOIN t0 on problems.id = t0.id"));
        assertTrue(sql.contains("JOIN t1 on problems.id = t1.id"));
        assertTrue(sql.contains("SELECT *, status FROM " + Problems.TABLE_NAME));
        assertTrue(sql.endsWith(";"));
    }

    @Test
    void testToSQLPreparedStatement_SetsAllParametersInOrder() throws SQLException {
        Parameter p1 = mock(Parameter.class);
        Parameter p2 = mock(Parameter.class);

        Filter f1 = mock(Filter.class);
        Filter f2 = mock(Filter.class);

        when(f1.toSQLStatement()).thenReturn("SELECT id, 'status1' AS status FROM problems WHERE id = ?");
        when(f2.toSQLStatement()).thenReturn("SELECT id, 'status2' AS status FROM problems WHERE name = ?");

        when(f1.getParameters()).thenReturn(List.of(p1));
        when(f2.getParameters()).thenReturn(List.of(p2));

        FilterAndLoggedIn filter = new FilterAndLoggedIn();
        filter.addFilter(f1);
        filter.addFilter(f2);

        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);

        PreparedStatement resultPs = filter.toSQLPreparedStatement(conn);

        assertNotNull(resultPs);
        verify(conn).prepareStatement(anyString());
        verify(p1).setParameter(eq(1), eq(ps));
        verify(p2).setParameter(eq(2), eq(ps));
    }

    @Test
    void testGetParameters_ReturnsEmptyList() {
        FilterAndLoggedIn filter = new FilterAndLoggedIn();
        assertTrue(filter.getParameters().isEmpty());
    }
}
