package com.example.submissions.DAO;

import com.example.constants.DatabaseConstants.*;

import static java.lang.String.format;

public class ToSQL {

    //INSERT INTO test_cases (problem_id, problem_input, problem_output, test_number) VALUES (1, '1 2', '3', 1);
    public static String toInsertTestCaseSQL() {
        return format("INSERT INTO %s " +
                "(%s, %s, %s, %s) " +
                "VALUES (?, ?, ?, ?);",
                TestCases.TABLE_NAME,
                TestCases.COL_PROBLEM_ID,
                TestCases.COL_INPUT,
                TestCases.COL_OUTPUT,
                TestCases.COL_TEST_NUMBER
        );
    }

    public static String toTestCasesSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                TestCases.TABLE_NAME,
                TestCases.TABLE_NAME,
                TestCases.COL_PROBLEM_ID);
    }
    public static String toVerdictByNameSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.COL_VERDICT
        );
    }

    public static String toVerdictByIdSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.TABLE_NAME,
                SubmissionVerdict.COL_ID
        );
    }
    public static String toCodeLanguageByNameSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                CodeLanguages.TABLE_NAME,
                CodeLanguages.TABLE_NAME,
                CodeLanguages.COL_LANGUAGE
        );
    }

    public static String toCodeLanguageByIdSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                CodeLanguages.TABLE_NAME,
                CodeLanguages.TABLE_NAME,
                CodeLanguages.COL_ID
        );
    }
    public static String toInsertSubmissionNoIdSQL() {
        return format("INSERT INTO %s " +
                "(%s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);",
                Submissions.TABLE_NAME,
                Submissions.COL_USER_ID,
                Submissions.COL_PROBLEM_ID,
                Submissions.COL_VERDICT_ID,
                Submissions.COL_SOLUTION,
                Submissions.COL_CODE_LANGUAGE_ID,
                Submissions.COL_TIME,
                Submissions.COL_MEMORY,
                Submissions.COL_SUBMIT_DATE,
                Submissions.COL_LOG
        );
    }
    public static String toUpdateSubmissionSQL() {
        return format("UPDATE %s SET " +
                "%s.%s = ?, " +
                "%s.%s = ?, " +
                "%s.%s = ?, " +
                "%s.%s = ?, " +
                "%s.%s = ?, " +
                "%s.%s = ?, " +
                "%s.%s = ?, " +
                "%s.%s = ?, " +
                "%s.%s = ? " +
                "WHERE %s.%s = ?;",
                Submissions.TABLE_NAME,
                Submissions.TABLE_NAME, Submissions.COL_USER_ID,
                Submissions.TABLE_NAME, Submissions.COL_PROBLEM_ID,
                Submissions.TABLE_NAME, Submissions.COL_VERDICT_ID,
                Submissions.TABLE_NAME, Submissions.COL_SOLUTION,
                Submissions.TABLE_NAME, Submissions.COL_CODE_LANGUAGE_ID,
                Submissions.TABLE_NAME, Submissions.COL_TIME,
                Submissions.TABLE_NAME, Submissions.COL_MEMORY,
                Submissions.TABLE_NAME, Submissions.COL_SUBMIT_DATE,
                Submissions.TABLE_NAME, Submissions.COL_LOG,
                Submissions.TABLE_NAME, Submissions.COL_ID
        );
    }
    public static String toSubmissionSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ? AND %s.%s = ?;",
                Submissions.TABLE_NAME,
                Submissions.TABLE_NAME,
                Submissions.COL_USER_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_PROBLEM_ID
        );
    }

    public static String toSubmissionSortedSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ? AND %s.%s = ? ORDER BY %s.%s DESC;",
                Submissions.TABLE_NAME,
                Submissions.TABLE_NAME,
                Submissions.COL_USER_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_PROBLEM_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_SUBMIT_DATE
        );
    }
    public static String toStatusSQL(){
        return format("SELECT * FROM %s;",
                ProblemStatus.TABLE_NAME
        );
    }
    public static String toStatusByIdSQL(){
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
               ProblemStatus.TABLE_NAME,
               ProblemStatus.TABLE_NAME,
               ProblemStatus.COL_ID
        );
    }
    public static String toStatusByNameSQL(){
        return format("SELECT * FROM %s WHERE %s.%s = ?;",
                ProblemStatus.TABLE_NAME,
                ProblemStatus.TABLE_NAME,
                ProblemStatus.COL_STATUS
        );
    }
    public static String toStatusByIds(){
        return "";
    }
    public static String toCodeLanguageSQL() {
        return format("SELECT * FROM %s;",
                CodeLanguages.TABLE_NAME);
    }

    public static String toSubmissionByDateSortedSQL() {
        return format("SELECT * FROM %s WHERE %s.%s = ? AND DAY(%s.%s) = ? AND MONTH(%s.%s) = ? AND YEAR(%s.%s) = ? ORDER BY %s.%s DESC;",
                Submissions.TABLE_NAME,
                Submissions.TABLE_NAME,
                Submissions.COL_USER_ID,
                Submissions.TABLE_NAME,
                Submissions.COL_SUBMIT_DATE,
                Submissions.TABLE_NAME,
                Submissions.COL_SUBMIT_DATE,
                Submissions.TABLE_NAME,
                Submissions.COL_SUBMIT_DATE,
                Submissions.TABLE_NAME,
                Submissions.COL_SUBMIT_DATE
        );
    }

    public static String toAllSubmissionSortedSQL() {
        return format("SELECT * FROM %s ORDER BY %s.%s DESC",
                Submissions.TABLE_NAME,
                Submissions.TABLE_NAME,
                Submissions.COL_SUBMIT_DATE
        );
    }
}
