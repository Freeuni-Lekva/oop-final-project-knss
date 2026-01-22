package com.example.admin.util;

import com.example.constants.DatabaseConstants;

import static java.lang.String.format;

public class ToSQL {
    public static String getRoleByIdSQL() {
        return format("SELECT * FROM %s " +
                        "WHERE %s = ?",
                DatabaseConstants.UserRole.TABLE_NAME,
                DatabaseConstants.UserRole.COL_ID
        );
    }

    public static String toInsertProblemTopicRelationSQL() {
        return format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
                DatabaseConstants.ProblemManyToManyTopic.TABLE_NAME,
                DatabaseConstants.ProblemManyToManyTopic.COL_PROBLEM_ID,
                DatabaseConstants.ProblemManyToManyTopic.COL_TOPIC_ID
        );
    }
}
