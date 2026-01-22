package com.example.problems.Filters;


import com.example.problems.DTO.Status;
import com.example.problems.Filters.Parameters.Parameter;
import com.example.problems.Filters.Parameters.ParameterInteger;
import com.example.registration.DTO.User;
import com.example.constants.DatabaseConstants.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.lang.String.format;

public class FilterStatus implements Filter {

    private final User user;
    private final Status status;

    public FilterStatus(User user, Status status) {
        this.user = user;
        this.status = status;
    }

    public String toSolvedProblemsSQL() {
        return format("SELECT %s.*, 'Accepted' AS status FROM %s JOIN %s ON %s.%s = %s.%s JOIN %s on %s.%s = %s.%s" +
                "WHERE %s.%s = ? AND %s.%s = ?",
                Problems.TABLE_NAME,
                Problems.TABLE_NAME,
                Submissions.TABLE_NAME,
                Problems.TABLE_NAME,
                Problems.COL_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_PROBLEM_ID,
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.COL_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_VERDICT_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_USER_ID,
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.COL_VERDICT
        );
    }

    public String toAttemptedProblemsSQL() {
        return format("SELECT %s.*, 'Rejected' AS status FROM %s JOIN %s ON %s.%s = %s.%s " +
                        "JOIN %s ON %s.%s = %s.%s" +
                        "WHERE %s.%s = ? GROUP BY %s.%s HAVING SUM(%s.%s = ?) = 0",
                 Problems.TABLE_NAME,
                 Problems.TABLE_NAME,
                 Submissions.TABLE_NAME,
                 Problems.TABLE_NAME,
                 Problems.COL_ID,
                 Submissions.TABLE_NAME,
                 Submissions.COL_PROBLEM_ID,
                 SubmissionVerdict.TABLE_NAME,
                 SubmissionVerdict.TABLE_NAME,
                 SubmissionVerdict.COL_ID,
                 Submissions.TABLE_NAME,
                 Submissions.COL_VERDICT_ID,
                 Submissions.TABLE_NAME,
                 Submissions.COL_USER_ID,
                 Submissions.TABLE_NAME,
                 Submissions.COL_PROBLEM_ID,
                 SubmissionVerdict.TABLE_NAME,
                 SubmissionVerdict.COL_VERDICT
        );
    }
    public String toToDoProblemsSQL(){
        return format("SELECT %s.*, 'Todo' AS status FROM %s LEFT JOIN %s ON %s.%s = %s.%s" +
                "AND %s.%s = ? WHERE %s.%s IS NULL",
                Problems.TABLE_NAME,
                Problems.TABLE_NAME,
                Submissions.TABLE_NAME,
                Submissions.TABLE_NAME,
                Submissions.COL_PROBLEM_ID,
                Problems.TABLE_NAME,
                Problems.COL_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_USER_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_PROBLEM_ID
        );
    }

    public String ProbemsStatuses(){
        return format(toSolvedProblemsSQL() + " UNION ALL " +
                toAttemptedProblemsSQL() + " UNION ALL "+
                toToDoProblemsSQL()
        );
    }
    @Override
    public String toSQLStatement() {
        if(status.getStatus().equals("Accepted")){
            return toSolvedProblemsSQL();
        }
        if(status.getStatus().equals("Rejected")){
            return toAttemptedProblemsSQL();
        }
        if(status.getStatus().equals("Todo")){
            return toToDoProblemsSQL();
        }
        return "";
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
                new ParameterInteger(status.getId()),
                new ParameterInteger(status.getId()),
                new ParameterInteger(status.getId())
        );
    }
}
