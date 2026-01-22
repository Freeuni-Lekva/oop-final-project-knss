package com.example.constants;

public class DatabaseConstants {

    public static final class Users {
        public static final String TABLE_NAME                       = "users";
        public static final String COL_ID                           = "id";
        public static final String COL_ROLE_ID                      = "role_id";
        public static final String COL_MAIL                         = "mail";
        public static final String COL_USERNAME                     = "username";
        public static final String COL_PASSWORD_HASH                = "password_hash";
        public static final String COL_IS_VERIFIED                  = "is_verified";
        public static final String COL_VERIFICATION_TOKEN           = "verification_token";
        public static final String COL_VERIFICATION_TOKEN_EXPIRY    = "verification_token_expiry";
        public static final String COL_REGISTER_DATE                = "register_date";
    }

    public static final class UserRole {
        public static final String TABLE_NAME = "user_role";
        public static final String COL_ID     = "id";
        public static final String COL_ROLE   = "role";
    }

    public static final class ProblemDifficulty {
        public static final String TABLE_NAME  = "problem_difficulty";
        public static final String COL_ID      = "id";
        public static final String COL_DIFFICULTY = "difficulty";
    }

    public static final class ProblemTopic {
        public static final String TABLE_NAME = "problem_topic";
        public static final String COL_ID     = "id";
        public static final String COL_TOPIC  = "topic";
    }

    public static final class ProblemStatus {
        public static final String TABLE_NAME = "problem_status";
        public static final String COL_ID     = "id";
        public static final String COL_STATUS = "status";
    }

    public static final class Problems {
        public static final String TABLE_NAME        = "problems";
        public static final String COL_ID            = "id";
        public static final String COL_TITLE         = "problem_title";
        public static final String COL_DESCRIPTION   = "problem_description";
        public static final String COL_DIFFICULTY_ID = "difficulty_id";
        public static final String COL_CREATE_DATE   = "create_date";
        public static final String COL_INPUT_SPEC   = "input_spec";
        public static final String COL_OUTPUT_SPEC   = "output_spec";
        public static final String COL_TIME_LIMIT    = "time_limit";
        public static final String COL_MEMORY_LIMIT    = "memory_limit";
    }

    public static final class ProblemManyToManyTopic {
        public static final String TABLE_NAME      = "problem_many_to_many_topic";
        public static final String COL_ID          = "id";
        public static final String COL_PROBLEM_ID  = "problem_id";
        public static final String COL_TOPIC_ID    = "topic_id";
    }
    public static final class Submissions {

        public static final String TABLE_NAME        = "submissions";
        public static final String COL_ID            = "id";
        public static final String COL_USER_ID       = "user_id";
        public static final String COL_PROBLEM_ID    = "problem_id";
        public static final String COL_VERDICT_ID    = "verdict_id";
        public static final String COL_SOLUTION      = "solution_code";
        public static final String COL_CODE_LANGUAGE_ID = "code_language_id";
        public static final String COL_TIME          = "time";
        public static final String COL_MEMORY        = "memory";
        public static final String COL_SUBMIT_DATE   = "submit_date";
        public static final String COL_LOG           = "log";

    }

    public static final class TestCases {
        public static final String TABLE_NAME     = "test_cases";
        public static final String COL_ID         = "id";
        public static final String COL_PROBLEM_ID = "problem_id";
        public static final String COL_INPUT      = "problem_input";
        public static final String COL_OUTPUT     = "problem_output";
        public static final String COL_TEST_NUMBER = "test_number";
    }

    public static final class SubmissionVerdict {
        public static final String TABLE_NAME  = "submission_verdict";
        public static final String COL_ID        = "id";
        public static final String COL_VERDICT = "verdict";
    }

    public static final class CodeLanguages {
        public static final String TABLE_NAME  = "code_language";
        public static final String COL_ID        = "id";
        public static final String COL_LANGUAGE = "language";
    }


    public static final class DifficultyElements {
        public static final String DIFFICULTY_EASY = "Easy";
        public static final String DIFFICULTY_MEDIUM = "Medium";
        public static final String DIFFICULTY_HARD = "Hard";
    }

    public static final class ProblemVerdictElements {
        public static final String VERDICT_ACCEPTED = "Accepted";
        public static final String VERDICT_REJECTED = "Rejected";
        public static final String VERDICT_TIME_LIMIT_EXCEEDED = "Time Limit Exceeded";
    }

}

