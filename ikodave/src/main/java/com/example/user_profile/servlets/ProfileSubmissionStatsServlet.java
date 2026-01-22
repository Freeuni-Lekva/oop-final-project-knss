package com.example.user_profile.servlets;

import com.example.problems.DAO.DifficultyDAO;
import com.example.problems.DTO.Difficulty;
import com.example.registration.dao.UserDAO;
import com.example.registration.DTO.User;
import com.example.submissions.DAO.VerdictDAO;
import com.example.submissions.DTO.SubmissionVerdict;
import com.example.user_profile.Response.UserSubmissionStats;
import com.example.user_profile.Response.UsernameBody;
import com.example.user_profile.dao.ProblemStatsDAO;
import com.example.user_profile.dao.UserStatsDAO;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.constants.AttributeConstants.*;
import static com.example.constants.DatabaseConstants.DifficultyElements.*;
import static com.example.constants.DatabaseConstants.ProblemVerdictElements.VERDICT_ACCEPTED;

public class ProfileSubmissionStatsServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDAO userDAO = (UserDAO) getServletContext().getAttribute(USER_DAO_KEY);
        UserStatsDAO userStatsDAO = (UserStatsDAO) getServletContext().getAttribute(USER_STATS_DAO);
        ProblemStatsDAO problemStatsDAO = (ProblemStatsDAO) getServletContext().getAttribute(PROBLEM_STATS_DAO);
        DifficultyDAO difficultyDAO = (DifficultyDAO) getServletContext().getAttribute(DIFFICULTY_DAO_KEY);
        VerdictDAO verdictDAO = (VerdictDAO) getServletContext().getAttribute(VERDICT_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);

        UsernameBody usernameBody = gson.fromJson(request.getReader(), UsernameBody.class);
        String username = usernameBody.getUsername();
        User user = userDAO.getUserByUsername(username);

        if (user == null || !user.isVerified()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserSubmissionStats userSubmissionStats = new UserSubmissionStats();

        Difficulty difficultyEasy = difficultyDAO.getDifficultyByName(DIFFICULTY_EASY);
        Difficulty difficultyMedium = difficultyDAO.getDifficultyByName(DIFFICULTY_MEDIUM);
        Difficulty difficultyHard = difficultyDAO.getDifficultyByName(DIFFICULTY_HARD);
        SubmissionVerdict verdictAccepted = verdictDAO.getVerdictByName(VERDICT_ACCEPTED);

        int easySolvedCount = userStatsDAO.getSolvedProblemCountByDifficulty(user, difficultyEasy);
        int easyTotalCount = problemStatsDAO.getProblemCountByDifficulty(difficultyEasy);
        userSubmissionStats.setEasySolvedProblemsCount(easySolvedCount);
        userSubmissionStats.setEasyNotSolvedProblemsCount(easyTotalCount - easySolvedCount);

        int mediumSolvedCount = userStatsDAO.getSolvedProblemCountByDifficulty(user, difficultyMedium);
        int mediumTotalCount = problemStatsDAO.getProblemCountByDifficulty(difficultyMedium);
        userSubmissionStats.setMediumSolvedProblemsCount(mediumSolvedCount);
        userSubmissionStats.setMediumNotSolvedProblemsCount(mediumTotalCount - mediumSolvedCount);

        int hardSolvedCount = userStatsDAO.getSolvedProblemCountByDifficulty(user, difficultyHard);
        int hardTotalCount = problemStatsDAO.getProblemCountByDifficulty(difficultyHard);
        userSubmissionStats.setHardSolvedProblemsCount(hardSolvedCount);
        userSubmissionStats.setHardNotSolvedProblemsCount(hardTotalCount - hardSolvedCount);

        int submissionsTotalCount = userStatsDAO.getSubmissionsCount(user);
        int acceptedSubmissionsCount = userStatsDAO.getSubmittedProblemCountByVerdict(user, verdictAccepted);
        userSubmissionStats.setSubmissionsTotalCount(submissionsTotalCount);
        userSubmissionStats.setNotAcceptedSubmissionsCount(submissionsTotalCount - acceptedSubmissionsCount);

        int submissionsToday = userStatsDAO.getSubmissionsCountByDays(user);
        userSubmissionStats.setAcceptedProblemsCountToday(submissionsToday);

        int userRank = userStatsDAO.getUserRank(user);
        userSubmissionStats.setUserRank(userRank);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(userSubmissionStats));
    }

}