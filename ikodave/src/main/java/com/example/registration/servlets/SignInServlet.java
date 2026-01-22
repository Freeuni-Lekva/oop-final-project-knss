package com.example.registration.servlets;

import com.example.registration.dao.UserDAO;
import com.example.registration.DTO.User;
import com.example.registration.Responce.UserSignInInput;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.registration.servlets.Authentication.*;
import static com.example.constants.AttributeConstants.*;
import static com.example.constants.SessionConstants.USER_KEY;

public class SignInServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!redirectProfileIfSignedIn(request, response)) {
                request.getRequestDispatcher("/static/authentication/signin.html")
                        .forward(request, response);
            }
        } catch (ServletException e) {
            throw new IOException("Forwarding failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        UserDAO userDao = (UserDAO) request.getServletContext().getAttribute(USER_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);

        if (redirectProfileIfSignedIn(request, response)) return;

        UserSignInInput userSignInInput = gson.fromJson(request.getReader(), UserSignInInput.class);
        String username = userSignInInput.getUsername();
        String password = userSignInInput.getPassword();
        User user = userDao.getUserByUsername(username);

        Map<String, String> registrationResult = new HashMap<>();

        if (user == null) {
            registrationResult.put("status", "invalid");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(registrationResult));
            return;
        }

        boolean authSuccess = BCrypt.checkpw(password, user.getPasswordHash());

        if (authSuccess) {
            request.getSession().setAttribute(USER_KEY, user);
            registrationResult.put("status", "ok");
        } else {
            registrationResult.put("status", "invalid");
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(registrationResult));
    }

}