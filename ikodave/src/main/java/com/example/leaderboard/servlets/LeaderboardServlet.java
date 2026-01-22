package com.example.leaderboard.servlets;

import com.example.leaderboard.dao.LeaderboardDAO;
import com.example.leaderboard.dto.UserWithScore;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.example.constants.AttributeConstants.GSON_KEY;
import static com.example.constants.AttributeConstants.LEADERBOARD_DAO_KEY;

public class LeaderboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);
        LeaderboardDAO leaderboardDAO = (LeaderboardDAO) getServletContext().getAttribute(LEADERBOARD_DAO_KEY);

        // Now returns List<UserWithRank>
        List<UserWithScore> leaderboard = leaderboardDAO.getUsersByScore();

        String json = gson.toJson(leaderboard);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}
