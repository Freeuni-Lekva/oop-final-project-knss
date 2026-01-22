package com.example.registration.servlets;

import com.example.registration.dao.UserDAO;
import com.example.registration.DTO.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static com.example.constants.AttributeConstants.USER_DAO_KEY;
import static com.example.constants.SessionConstants.USER_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegistrationServletServletTest {

    private RegistrationServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private ServletContext servletContext;
    private UserDAO userDao;

    @BeforeEach
    void setUp() {
        servlet = new RegistrationServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        servletContext = mock(ServletContext.class);
        userDao = mock(UserDAO.class);

        when(request.getServletContext()).thenReturn(servletContext);
        when(request.getSession()).thenReturn(session);
        when(servletContext.getAttribute(USER_DAO_KEY)).thenReturn(userDao);
    }

    @Test
    void testDoPost_SuccessfulRegistration() throws Exception {
        // Prepare input JSON body for new user
        String json = "{\"username\":\"newuser\",\"password\":\"plainpass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        // Mock DAO behavior
        when(userDao.usernameExists("newuser")).thenReturn(false);

        // Mock getUserByUsername to return the new user with roleId set to avoid NPE
        User returnedUser = new User("newuser", "hashedpass");
        returnedUser.setRoleId(1);
        when(userDao.getUserByUsername("newuser")).thenReturn(returnedUser);

        // Capture output
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        // Call servlet
        servlet.doPost(request, response);

        // Verify addUser called with hashed password (not plain)
        verify(userDao).addUser(argThat(u ->
                u.getUsername().equals("newuser") &&
                        !u.getPasswordHash().equals("plainpass")
        ));

        // Verify session attribute is set with the User returned by DAO
        verify(session).setAttribute(eq(USER_KEY), eq(returnedUser));

        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("\"status\":\"ok\""));
    }

    @Test
    void testDoPost_UserAlreadyExists_SendsExistsStatus() throws Exception {
        // Prepare input JSON body for existing user
        String json = "{\"username\":\"existingUser\",\"password\":\"password\"}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        // User exists in DB
        when(userDao.usernameExists("existingUser")).thenReturn(true);

        // Mock getUserByUsername returns non-null User to avoid NPE
        User existingUser = new User("existingUser", "hashedPassword");
        existingUser.setRoleId(1);
        when(userDao.getUserByUsername("existingUser")).thenReturn(existingUser);

        // Capture output
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        // Call servlet
        servlet.doPost(request, response);

        // Should never call addUser for existing user
        verify(userDao, never()).addUser(any());

        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("\"status\":\"exists\""));
    }

    @Test
    void testDoGet_ForwardToRegistrationForm() throws Exception {
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getRequestDispatcher("/static/authentication/registration.html")).thenReturn(dispatcher);

        // Simulate user not logged in (session attribute null)
        when(session.getAttribute(USER_KEY)).thenReturn(null);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }


}
