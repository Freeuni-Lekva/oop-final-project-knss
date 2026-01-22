package com.example.user_profile.dao;

import com.example.problems.DTO.Difficulty;

public interface ProblemStatsDAO {

    int getProblemCountByDifficulty(Difficulty difficulty);
}
