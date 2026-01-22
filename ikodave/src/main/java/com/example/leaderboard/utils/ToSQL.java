package com.example.leaderboard.utils;

import com.example.constants.DatabaseConstants.*;

import static java.lang.String.format;

public class ToSQL {
    public static String getUsersScored() {
        return String.format(
                "SELECT u.%s AS \"USER\", " +
                        "(SELECT COUNT(DISTINCT s.%s) " +
                        "FROM %s uc " +
                        "LEFT JOIN %s s ON s.%s = uc.%s " +
                        "JOIN %s v ON v.%s = s.%s " +
                        "WHERE v.%s = 'Accepted' AND uc.%s = u.%s) AS SCORE " +
                        "FROM %s u " +
                        "WHERE u.%s = TRUE " +
                        "ORDER BY SCORE DESC;",
                Users.COL_USERNAME,
                Submissions.COL_PROBLEM_ID,
                Users.TABLE_NAME,
                Submissions.TABLE_NAME,
                Submissions.COL_USER_ID,
                Users.COL_ID,
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.COL_ID,
                Submissions.COL_VERDICT_ID,
                SubmissionVerdict.COL_VERDICT,
                Users.COL_ID,
                Users.COL_ID,
                Users.TABLE_NAME,
                Users.COL_IS_VERIFIED
        );
    }

}
