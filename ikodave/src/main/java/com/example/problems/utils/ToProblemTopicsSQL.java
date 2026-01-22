package com.example.problems.utils;

import com.example.constants.DatabaseConstants.*;

import static java.lang.String.format;

public class ToProblemTopicsSQL {

    public static String toProblemTopicsSQL() {
        return format("SELECT * FROM %s;", ProblemTopic.TABLE_NAME);
    }

    public static String toProblemTopicsByIdSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                ProblemTopic.TABLE_NAME,
                ProblemTopic.TABLE_NAME,
                ProblemTopic.COL_ID);
    }

    public static String toProblemTopicsByTopicNameSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                ProblemTopic.TABLE_NAME,
                ProblemTopic.TABLE_NAME,
                ProblemTopic.COL_TOPIC);
    }
}
