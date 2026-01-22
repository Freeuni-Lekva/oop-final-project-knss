package com.example.problems.Filters;

import com.example.problems.DTO.Difficulty;
import com.example.problems.Filters.Parameters.Parameter;
import com.example.problems.Filters.Parameters.ParameterInteger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.example.constants.DatabaseConstants.*;
import static java.lang.String.format;

public class FilterDifficulty implements Filter {

    private final Difficulty difficulty;

    public FilterDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toSQLStatement() {
        return format(
                "SELECT * FROM %s WHERE %s.%s = ?",
                Problems.TABLE_NAME,
                Problems.TABLE_NAME,
                Problems.COL_DIFFICULTY_ID
        );
    }

    @Override
    public PreparedStatement toSQLPreparedStatement(Connection connection) {
        String sqlStatement = toSQLStatement();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sqlStatement);
            int index = 1;
            for (Parameter parameter : getParameters()) {
                parameter.setParameter(index++, preparedStatement);
            }
            return preparedStatement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Parameter> getParameters() {
        return List.of(new ParameterInteger(difficulty.getId()));
    }

}
