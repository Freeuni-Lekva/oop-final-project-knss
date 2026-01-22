package com.example.submissions.servlets;

import com.example.registration.DTO.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.constants.SessionConstants.USER_KEY;

public class SubmissionsListPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(USER_KEY);
        if (user == null) {
            response.sendRedirect("/sigin");
        }
        else {
            request.getRequestDispatcher("/static/submissions/submissions.html").forward(request, response);
        }
    }

}
