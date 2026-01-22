package com.example.admin.servlets;

import com.example.admin.dao.RoleDAO;
import com.example.admin.dto.Role;
import com.example.registration.DTO.User;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static com.example.constants.AttributeConstants.ROLE_DAO_KEY;
import static com.example.constants.SessionConstants.USER_KEY;

public class AdminAuthHelper {
    public static boolean isAdmin(HttpServletRequest request, ServletContext context) {
        User user = (User) request.getSession().getAttribute(USER_KEY);
        if (user == null) return false;

        RoleDAO roleDAO = (RoleDAO) context.getAttribute(ROLE_DAO_KEY);
        Role role = roleDAO.getRoleById(user.getRoleId());
        return role != null && "Admin".equals(((Role) role).getRole());
    }
}
