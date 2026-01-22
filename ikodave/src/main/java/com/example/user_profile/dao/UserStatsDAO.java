package com.example.user_profile.dao;

import com.example.problems.DTO.Difficulty;
import com.example.registration.DTO.User;
import com.example.submissions.DTO.SubmissionVerdict;

import java.sql.Timestamp;
import java.util.List;

public interface UserStatsDAO {

    int getSubmittedProblemCountByVerdict(User user, SubmissionVerdict verdict);

    int getSolvedProblemCount(User user);

    int getSolvedProblemCountByDifficulty(User user, Difficulty difficulty);

    int getSubmissionsCount(User user);

    int getSubmissionsCountByDays(User user);

    int getUserRank(User user);

    List<Timestamp> getUserActivityByMonth(User user, int month, int year);
}