package com.example.problems.Filters;

import com.example.problems.Filters.Parameters.Parameter;
import com.example.problems.Filters.Parameters.ParameterInteger;
import com.example.problems.Filters.Parameters.ParameterString;
import com.example.registration.DTO.User;
import com.example.constants.DatabaseConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.lang.String.format;

public class FilterStatusAttempted implements Filter{
    private final User user;

    public FilterStatusAttempted(User user) {
        this.user = user;
    }
    @Override
    public String toSQLStatement() {
        return format("SELECT %s.*, 'Attempted' AS status FROM %s JOIN %s ON %s.%s = %s.%s " +
                        "JOIN %s ON %s.%s = %s.%s " +
                        "WHERE %s.%s = ? GROUP BY %s.%s HAVING SUM(%s.%s = ?) = 0",
                DatabaseConstants.Problems.TABLE_NAME,
                DatabaseConstants.Problems.TABLE_NAME,
                DatabaseConstants.Submissions.TABLE_NAME,
                DatabaseConstants.Problems.TABLE_NAME,
                DatabaseConstants.Problems.COL_ID,
                DatabaseConstants.Submissions.TABLE_NAME,
                DatabaseConstants.Submissions.COL_PROBLEM_ID,
                DatabaseConstants.SubmissionVerdict.TABLE_NAME,
                DatabaseConstants.SubmissionVerdict.TABLE_NAME,
                DatabaseConstants.SubmissionVerdict.COL_ID,
                DatabaseConstants.Submissions.TABLE_NAME,
                DatabaseConstants.Submissions.COL_VERDICT_ID,
                DatabaseConstants.Submissions.TABLE_NAME,
                DatabaseConstants.Submissions.COL_USER_ID,
                DatabaseConstants.Submissions.TABLE_NAME,
                DatabaseConstants.Submissions.COL_PROBLEM_ID,
                DatabaseConstants.SubmissionVerdict.TABLE_NAME,
                DatabaseConstants.SubmissionVerdict.COL_VERDICT
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

    @Override
    public List<Parameter> getParameters() {
        return List.of(new ParameterInteger(user.getId()),
                       new ParameterString("Accepted"));
    }
}
