package com.example.submissions.DAO;


import com.example.submissions.DTO.CodeLanguage;
import com.example.submissions.DTO.Submission;
import com.example.submissions.DTO.TestCase;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.constants.DatabaseConstants;
import com.example.constants.DatabaseConstants.*;
import com.example.submissions.DTO.SubmissionVerdict;

public class ToDTO {

    public static TestCase toTestCase(ResultSet resultSet) throws SQLException {
        return new TestCase(
                resultSet.getInt(TestCases.COL_ID),
                resultSet.getInt(TestCases.COL_PROBLEM_ID),
                resultSet.getInt(TestCases.COL_TEST_NUMBER),
                resultSet.getString(TestCases.COL_OUTPUT),
                resultSet.getString(TestCases.COL_INPUT)
        );
    }
    public static SubmissionVerdict toVerdict(ResultSet resultSet) throws SQLException {
        try {
            return new SubmissionVerdict(resultSet.getInt(DatabaseConstants.SubmissionVerdict.COL_ID), resultSet.getString(DatabaseConstants.SubmissionVerdict.COL_VERDICT));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Submission toSubmission(ResultSet resultSet) throws SQLException {
        try {
            Submission submission = new Submission();
            submission.setId(resultSet.getInt(Submissions.COL_ID));
            submission.setUserId(resultSet.getInt(Submissions.COL_USER_ID));
            submission.setProblemId(resultSet.getInt(Submissions.COL_PROBLEM_ID));
            submission.setVerdictId(resultSet.getInt(Submissions.COL_VERDICT_ID));
            submission.setCodeLanguageId(resultSet.getInt(Submissions.COL_CODE_LANGUAGE_ID));
            submission.setSolutionCode(resultSet.getString(Submissions.COL_SOLUTION));
            submission.setTime(resultSet.getLong(Submissions.COL_TIME));
            submission.setMemory(resultSet.getLong(Submissions.COL_MEMORY));
            submission.setSubmitDate(resultSet.getTimestamp(Submissions.COL_SUBMIT_DATE));
            submission.setLog(resultSet.getString(Submissions.COL_LOG));
            return submission;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static CodeLanguage toCodeLanguage(ResultSet resultSet) throws SQLException {
        try {
            CodeLanguage codeLanguage = new CodeLanguage();
            codeLanguage.setId(resultSet.getInt(CodeLanguages.COL_ID));
            codeLanguage.setLanguage(resultSet.getString(CodeLanguages.COL_LANGUAGE));
            return codeLanguage;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
