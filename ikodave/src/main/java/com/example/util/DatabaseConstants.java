package com.example.util;

public class DatabaseConstants {

    public static final class Users {
        public static final String TABLE_NAME        = "users";
        public static final String COL_ID            = "id";
        public static final String COL_RANK_ID       = "rank_id";
        public static final String COL_USERNAME      = "username";
        public static final String COL_PASSWORD      = "password";
        public static final String COL_REGISTER_DATE = "register_date";
    }

    public static final class UserRank {
        public static final String TABLE_NAME = "user_rank";
        public static final String COL_ID     = "id";
        public static final String COL_RANK   = "user_rank";
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
        public static final String COL_TIME_LIMIT    = "time_limit";
    }

    public static final class ProblemManyToManyTopic {
        public static final String TABLE_NAME      = "problem_many_to_many_topic";
        public static final String COL_ID          = "id";
        public static final String COL_PROBLEM_ID  = "problem_id";
        public static final String COL_TOPIC_ID    = "topic_id";
    }

    public static final class Submissions {
        public static final String TABLE_NAME      = "submissions";
        public static final String COL_ID          = "id";
        public static final String COL_USER_ID     = "user_id";
        public static final String COL_PROBLEM_ID  = "problem_id";
        public static final String COL_STATUS_ID   = "status_id";
        public static final String COL_SOLUTION    = "solution_code";
        public static final String COL_SUBMIT_DATE = "submit_date";
        public static final String COL_LOG         = "log";
    }

    public static final class TestCases {
        public static final String TABLE_NAME     = "test_cases";
        public static final String COL_ID         = "id";
        public static final String COL_PROBLEM_ID = "problem_id";
        public static final String COL_INPUT      = "problem_input";
        public static final String COL_OUTPUT     = "problem_output";
        public static final String COL_ORDER      = "order_num";
    }
}
