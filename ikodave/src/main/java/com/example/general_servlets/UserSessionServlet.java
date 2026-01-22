package com.example.general_servlets;

import com.example.admin.dao.RoleDAO;
import com.example.admin.dto.Role;
import com.example.registration.DTO.User;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.constants.AttributeConstants.GSON_KEY;
import static com.example.constants.AttributeConstants.ROLE_DAO_KEY;
import static com.example.constants.SessionConstants.USER_KEY;

public class UserSessionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user =  (User) request.getSession().getAttribute(USER_KEY);
        RoleDAO roleDAO = (RoleDAO) getServletContext().getAttribute(ROLE_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);

        Map<String, Object> userStatus = new HashMap<>();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (user == null) {
            userStatus.put("loggedIn", false);
            response.getWriter().write(gson.toJson(userStatus));
            return;
        }

        userStatus.put("loggedIn", true);
        userStatus.put("username", user.getUsername());
        userStatus.put("role", roleDAO.getRoleById(user.getRoleId()).getRole());
        userStatus.put("verified", user.isVerified());

        response.getWriter().write(gson.toJson(userStatus));
    }
}
