package com.example.problems.utils;

import com.example.constants.DatabaseConstants.*;

import static java.lang.String.format;

public class ToSQL {

    /**
     * INSERT INTO problems (
     * problem_title,
     * problem_description,
     * difficulty_id,
     * time_limit,
     * memory_limit,
     * input_spec,
     * output_spec,
     * create_date
     * ) VALUES
     * ('sum-of-two-numbers',
     * 'Given two integers a and b, output their sum.',
     * 1,
     * 1000,
     * 128,
     * 'Two integers a and b separated by a space. Bounds: -2,000,000,000 ≤ a, b ≤ 2,000,000,000.',
     * 'A single integer, the sum of a and b.',
     * NOW()),
     */

    public static String toInsertProblemSQL() {
        return format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) VALUES " +
                        "(?, ?, ?, ?, ?, ?, ?, NOW());",
                Problems.TABLE_NAME,
                Problems.COL_TITLE,
                Problems.COL_DESCRIPTION,
                Problems.COL_DIFFICULTY_ID,
                Problems.COL_TIME_LIMIT,
                Problems.COL_MEMORY_LIMIT,
                Problems.COL_INPUT_SPEC,
                Problems.COL_OUTPUT_SPEC,
                Problems.COL_CREATE_DATE
        );
    }

    public static String toProblemTopicsSQL() {
        return format("SELECT %s.* FROM %s JOIN %s ON %s.%s = %s.%s WHERE %s.%s = ?;",
                ProblemTopic.TABLE_NAME,
                ProblemTopic.TABLE_NAME,
                ProblemManyToManyTopic.TABLE_NAME,
                ProblemManyToManyTopic.TABLE_NAME,
                ProblemManyToManyTopic.COL_TOPIC_ID,
                ProblemTopic.TABLE_NAME,
                ProblemTopic.COL_ID,
                ProblemManyToManyTopic.TABLE_NAME,
                ProblemManyToManyTopic.COL_PROBLEM_ID
        );
    }

    public static String toProblemDifficultySQL() {
        return format("SELECT %s.* FROM %s JOIN %s ON %s.%s = %s.%s WHERE %s.%s = ?;",
                ProblemDifficulty.TABLE_NAME,
                ProblemDifficulty.TABLE_NAME,
                Problems.TABLE_NAME,
                Problems.TABLE_NAME,
                Problems.COL_DIFFICULTY_ID,
                ProblemDifficulty.TABLE_NAME,
                ProblemDifficulty.COL_ID,
                Problems.TABLE_NAME,
                Problems.COL_ID
        );
    }


    public static String toProblemStatusCountsSQL() {
        return format("SELECT SUM(%s.%s = ?) as accepts, " +
                        "SUM(%s.%s <> ?) as notAccepts " +
                        "FROM %s JOIN %s ON %s.%s = %s.%s " +
                        "WHERE %s.%s = ? AND %s.%s = ? " +
                        "GROUP BY %s.%s, %s.%s",
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.COL_VERDICT,
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.COL_VERDICT,
                Submissions.TABLE_NAME,
                SubmissionVerdict.TABLE_NAME,
                Submissions.TABLE_NAME,
                Submissions.COL_VERDICT_ID,
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.COL_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_PROBLEM_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_USER_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_USER_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_PROBLEM_ID
        );
    }

    public static String toProblemTitleSQL() {
        return format("SELECT %s FROM %s WHERE %s.%s = ?;",
                Problems.COL_TITLE,
                Problems.TABLE_NAME,
                Problems.TABLE_NAME,
                Problems.COL_ID
        );
    }

    public static String toProblemIdSQL() {
        return format("SELECT %s FROM %s WHERE %s.%s = ?;",
                Problems.COL_ID,
                Problems.TABLE_NAME,
                Problems.TABLE_NAME,
                Problems.COL_TITLE
        );
    }

    public static String toProblemByTitleSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                Problems.TABLE_NAME,
                Problems.TABLE_NAME,
                Problems.COL_TITLE
        );
    }


    public static String toDifficultyId() {
        return "";
    }

}
