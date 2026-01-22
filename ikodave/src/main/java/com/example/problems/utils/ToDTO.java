package com.example.problems.utils;

import com.example.problems.DTO.*;
import com.example.problems.FrontResponse.ProblemListResponse;
import com.example.registration.DTO.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static com.example.constants.DatabaseConstants.*;

public class ToDTO {

    public static User toUser(ResultSet rs) {
        try {
            User user = new User();
            user.setId(rs.getInt(Users.COL_ID));
            user.setRoleId(rs.getInt(Users.COL_ROLE_ID));
            user.setMail(rs.getString(Users.COL_MAIL));
            user.setUsername(rs.getString(Users.COL_USERNAME));
            user.setPasswordHash(rs.getString(Users.COL_PASSWORD_HASH));
            user.setIsVerified(rs.getBoolean(Users.COL_IS_VERIFIED));
            user.setVerificationCode(rs.getString(Users.COL_VERIFICATION_TOKEN));
            user.setVerificationCodeExpiry(rs.getObject(Users.COL_VERIFICATION_TOKEN_EXPIRY, LocalDateTime.class));
            user.setRegisterDate(rs.getTimestamp(Users.COL_REGISTER_DATE));
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Topic toTopic(ResultSet rs) {
        try {
            Topic topic = new Topic();
            topic.setId(rs.getInt(ProblemTopic.COL_ID));
            topic.setTopic(rs.getString(ProblemTopic.COL_TOPIC));
            return topic;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Difficulty toDifficulty(ResultSet rs) {
        try {
            Difficulty difficulty = new Difficulty();
            difficulty.setId(rs.getInt(ProblemDifficulty.COL_ID));
            difficulty.setDifficulty(rs.getString(ProblemDifficulty.COL_DIFFICULTY));
            return difficulty;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Status toStatus(ResultSet rs) {
        try {
            Status status = new Status();
            status.setId(rs.getInt(ProblemStatus.COL_ID));
            status.setStatus(rs.getString(ProblemStatus.COL_STATUS));
            return status;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ProblemListResponse toProblemListResponseLoggedIn(ResultSet resultSet) {
        try {
            ProblemListResponse problem = new ProblemListResponse();
            problem.setTitle(resultSet.getString(Problems.COL_TITLE));
            problem.setDifficultyId(resultSet.getInt(Problems.COL_DIFFICULTY_ID));
            problem.setStatus(resultSet.getString("status"));
            return problem;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ProblemListResponse toProblemListResponseLoggedOut(ResultSet resultSet) {
        try {
            ProblemListResponse problem = new ProblemListResponse();
            problem.setTitle(resultSet.getString(Problems.COL_TITLE));
            problem.setDifficultyId(resultSet.getInt(Problems.COL_DIFFICULTY_ID));
            problem.setStatus("No Status");
            return problem;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toStatusCounts(ResultSet rs) {
        try {
            if (!rs.next()) {
                return "Todo";
            }
            int acceptedCount = rs.getInt("accepts");
            int notAcceptedCount = rs.getInt("notAccepts");
            if (acceptedCount > 0) {
                return "Solved";
            }
            if (notAcceptedCount > 0) {
                return "Attempted";
            }
            return "";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Problem toProblem(ResultSet rs) {
        try {
            Problem problem = new Problem();
            problem.setId(rs.getInt(Problems.COL_ID));
            problem.setTitle(rs.getString(Problems.COL_TITLE));
            problem.setDescription(rs.getString(Problems.COL_DESCRIPTION));
            problem.setDifficultyId(rs.getInt(Problems.COL_DIFFICULTY_ID));
            problem.setCreateDate(rs.getTimestamp(Problems.COL_CREATE_DATE));
            problem.setInputSpec(rs.getString(Problems.COL_INPUT_SPEC));
            problem.setOutputSpec(rs.getString(Problems.COL_OUTPUT_SPEC));
            problem.setTimeLimit(rs.getLong(Problems.COL_TIME_LIMIT));
            problem.setMemoryLimit(rs.getLong(Problems.COL_MEMORY_LIMIT));
            return problem;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toTitle(ResultSet rs) {
        try {
            return rs.getString(Problems.COL_TITLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int toId(ResultSet rs) {
        try {
            return rs.getInt(Problems.COL_ID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
