package com.example.registration.servlets;

import com.example.registration.DTO.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

import static com.example.constants.SessionConstants.USER_KEY;


public class Authentication {
    public static boolean redirectProfileIfSignedIn(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        User user = (User) request.getSession().getAttribute(USER_KEY);
        if (user == null || !user.isVerified()) {
            return false;
        }
        request.getRequestDispatcher("/static/profile/profile_page.html").forward(request, response);
        return true;
    }

    private static final Gson gson = new Gson();

    protected static <T> T parseJsonBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return gson.fromJson(sb.toString(), clazz);
    }

    protected static void sendJsonResponse(HttpServletResponse response, Object body) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(body));
    }
}
