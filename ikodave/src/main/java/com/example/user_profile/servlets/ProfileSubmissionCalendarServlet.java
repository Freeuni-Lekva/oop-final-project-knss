package com.example.user_profile.servlets;

import com.example.registration.DTO.User;
import com.example.registration.dao.UserDAO;
import com.example.user_profile.Response.UserCalendarBody;
import com.example.user_profile.dao.UserStatsDAO;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static com.example.constants.AttributeConstants.*;

public class ProfileSubmissionCalendarServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDAO userDAO = (UserDAO) getServletContext().getAttribute(USER_DAO_KEY);
        UserStatsDAO userStatsDAO = (UserStatsDAO) getServletContext().getAttribute(USER_STATS_DAO);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);

        UserCalendarBody userCalendarBody = gson.fromJson(request.getReader(), UserCalendarBody.class);
        User user = userDAO.getUserByUsername(userCalendarBody.getUsername());

        if (user == null || !user.isVerified()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<Timestamp> submissionDates = userStatsDAO.getUserActivityByMonth(user, userCalendarBody.getMonth(), userCalendarBody.getYear());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(Map.of("submissionDates", submissionDates)));
    }

}
