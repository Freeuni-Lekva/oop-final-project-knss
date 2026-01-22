package com.example.admin.servlets;

import com.example.admin.dao.RoleDAO;
import com.example.admin.dto.Role;
import com.example.registration.DTO.User;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import static com.example.constants.AttributeConstants.ROLE_DAO_KEY;
import static com.example.constants.SessionConstants.USER_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminAuthAuthenticationTest {

    @Test
    void isAdmin_returnsFalse_whenUserIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletContext context = mock(ServletContext.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(USER_KEY)).thenReturn(null);

        boolean result = AdminAuthHelper.isAdmin(request, context);

        assertFalse(result);
    }

    @Test
    void isAdmin_returnsFalse_whenRoleDaoReturnsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletContext context = mock(ServletContext.class);
        HttpSession session = mock(HttpSession.class);
        RoleDAO roleDAO = mock(RoleDAO.class);

        User user = new User();
        user.setRoleId(1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(USER_KEY)).thenReturn(user);
        when(context.getAttribute(ROLE_DAO_KEY)).thenReturn(roleDAO);
        when(roleDAO.getRoleById(1)).thenReturn(null);

        boolean result = AdminAuthHelper.isAdmin(request, context);

        assertFalse(result);
    }

    @Test
    void isAdmin_returnsTrue_whenUserIsAdmin() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletContext context = mock(ServletContext.class);
        HttpSession session = mock(HttpSession.class);
        RoleDAO roleDAO = mock(RoleDAO.class);

        User user = new User();
        user.setRoleId(1);

        Role adminRole = new Role();
        adminRole.setRole("Admin");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(USER_KEY)).thenReturn(user);
        when(context.getAttribute(ROLE_DAO_KEY)).thenReturn(roleDAO);
        when(roleDAO.getRoleById(1)).thenReturn(adminRole);

        boolean result = AdminAuthHelper.isAdmin(request, context);

        assertTrue(result);
    }

    @Test
    void isAdmin_returnsFalse_whenRoleIsNotAdmin() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletContext context = mock(ServletContext.class);
        HttpSession session = mock(HttpSession.class);
        RoleDAO roleDAO = mock(RoleDAO.class);

        User user = new User();
        user.setRoleId(1);

        Role someRole = new Role();
        someRole.setRole("User");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(USER_KEY)).thenReturn(user);
        when(context.getAttribute(ROLE_DAO_KEY)).thenReturn(roleDAO);
        when(roleDAO.getRoleById(1)).thenReturn(someRole);

        boolean result = AdminAuthHelper.isAdmin(request, context);

        assertFalse(result);
    }
}
