package com.example.problems.Filters;

import com.example.problems.Filters.Parameters.Parameter;
import com.example.constants.DatabaseConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class FilterAndLoggedOut extends FilterAnd {

    private final List<Filter> filters;

    public FilterAndLoggedOut() {
        filters = new ArrayList<>();
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    @Override
    public String toSQLStatement() {
        StringBuilder sqlStatement = new StringBuilder();
        if (!filters.isEmpty()) {
            sqlStatement.append("WITH ");
        }

        for (int i = 0; i < filters.size(); i++) {
            sqlStatement.append(format("t%d AS ", i));
            sqlStatement.append("(");
            sqlStatement.append(filters.get(i).toSQLStatement());
            sqlStatement.append(")");
            if (i < filters.size() - 1) {
                sqlStatement.append(",");
            }
            sqlStatement.append(" \n");
        }

        StringBuilder selectStatement = new StringBuilder(format(
                "SELECT * FROM %s",
                DatabaseConstants.Problems.TABLE_NAME
        ));

        for (int i = 0; i < filters.size(); i++) {
            selectStatement.append(format(
                    " JOIN t%d on %s.%s = t%d.%s ",
                    i,
                    DatabaseConstants.Problems.TABLE_NAME,
                    DatabaseConstants.Problems.COL_ID,
                    i,
                    DatabaseConstants.Problems.COL_ID
            ));
        }

        selectStatement.append(";");

        sqlStatement.append(selectStatement);

        return sqlStatement.toString();
    }

    @Override
    public PreparedStatement toSQLPreparedStatement(Connection connection) {
        String sqlStatement = toSQLStatement();

        try  {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            int index = 1;
            for (Filter filter : filters) {
                List<Parameter> parameters = filter.getParameters();
                for (Parameter parameter : parameters) {
                    parameter.setParameter(index++, preparedStatement);
                }
            }
            return preparedStatement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Parameter> getParameters() {
        return List.of();
    }


}
