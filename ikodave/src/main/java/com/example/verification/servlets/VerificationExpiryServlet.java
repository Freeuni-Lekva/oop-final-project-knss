package com.example.verification.servlets;

import com.example.registration.DTO.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.example.constants.AttributeConstants.GSON_KEY;
import static com.example.constants.SessionConstants.USER_KEY;

public class VerificationExpiryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(USER_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);
        if (user == null || user.isVerified()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.getWriter().write(gson.toJson(Map.of("expiry", user.getVerificationCodeExpiry()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

}
