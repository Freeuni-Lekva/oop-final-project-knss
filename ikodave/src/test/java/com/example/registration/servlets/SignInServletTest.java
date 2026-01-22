package com.example.registration.servlets;

import com.example.registration.dao.UserDAO;
import com.example.registration.DTO.User;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static com.example.constants.AttributeConstants.*;
import static com.example.constants.SessionConstants.USER_KEY;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SignInServletTest {

    private SignInServlet signInServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServletContext servletContext;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private UserDAO userDAO;

    private Gson gson;

    @Before
    public void setUp() throws ServletException {
        signInServlet = new SignInServlet();
        gson = new Gson();

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        signInServlet.init(servletConfig);

        when(request.getSession()).thenReturn(session);
        lenient().when(request.getServletContext()).thenReturn(servletContext);
        lenient().when(servletContext.getAttribute(GSON_KEY)).thenReturn(gson);
        when(servletContext.getAttribute(USER_DAO_KEY)).thenReturn(userDAO);
    }

    @Test
    public void testDoGet_UserAlreadyLoggedIn() throws IOException, ServletException {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(session.getAttribute(USER_KEY)).thenReturn(mockUser);
        when(request.getRequestDispatcher("/static/profile/profile_page.html")).thenReturn(requestDispatcher);

        signInServlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request, never()).getRequestDispatcher("/static/authentication/signin.html");
    }

    @Test
    public void testDoGet_UserNotLoggedIn() throws IOException, ServletException {
        when(session.getAttribute(USER_KEY)).thenReturn(null);
        when(request.getRequestDispatcher("/static/authentication/signin.html")).thenReturn(requestDispatcher);

        signInServlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }

    @Test(expected = IOException.class)
    public void testDoGet_ForwardingException() throws IOException, ServletException {
        when(session.getAttribute(USER_KEY)).thenReturn(null);
        when(request.getRequestDispatcher("/static/authentication/signin.html")).thenReturn(requestDispatcher);
        doThrow(new ServletException("Forwarding failed")).when(requestDispatcher).forward(request, response);

        signInServlet.doGet(request, response);
    }

    @Test
    public void testDoPost_SuccessfulLogin() throws IOException, ServletException {
        String jsonInput = "{\"username\":\"testuser\",\"password\":\"testpass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getReader()).thenReturn(reader);
        when(response.getWriter()).thenReturn(writer);
        when(session.getAttribute(USER_KEY)).thenReturn(null);

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash(BCrypt.hashpw("testpass", BCrypt.gensalt()));

        when(userDAO.getUserByUsername("testuser")).thenReturn(mockUser);

        signInServlet.doPost(request, response);

        verify(response, atLeastOnce()).setContentType("application/json");
        verify(response, atLeastOnce()).setCharacterEncoding("UTF-8");
        verify(session).setAttribute(USER_KEY, mockUser);

        writer.flush();
        assertTrue(stringWriter.toString().contains("\"status\":\"ok\""));
    }

    @Test
    public void testDoPost_InvalidPassword() throws IOException, ServletException {
        String jsonInput = "{\"username\":\"testuser\",\"password\":\"wrongpass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getReader()).thenReturn(reader);
        when(response.getWriter()).thenReturn(writer);
        when(session.getAttribute(USER_KEY)).thenReturn(null);

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash(BCrypt.hashpw("testpass", BCrypt.gensalt()));

        when(userDAO.getUserByUsername("testuser")).thenReturn(mockUser);

        signInServlet.doPost(request, response);

        verify(session, never()).setAttribute(eq(USER_KEY), any());
        writer.flush();
        assertTrue(stringWriter.toString().contains("\"status\":\"invalid\""));
    }

    @Test
    public void testDoPost_UserNotFound() throws IOException, ServletException {
        String jsonInput = "{\"username\":\"nonexistent\",\"password\":\"testpass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getReader()).thenReturn(reader);
        when(response.getWriter()).thenReturn(writer);
        when(session.getAttribute(USER_KEY)).thenReturn(null);
        when(userDAO.getUserByUsername("nonexistent")).thenReturn(null);

        signInServlet.doPost(request, response);

        verify(session, never()).setAttribute(eq(USER_KEY), any());
        writer.flush();
        assertTrue(stringWriter.toString().contains("\"status\":\"invalid\""));
    }

    @Test
    public void testDoPost_MalformedJson() throws IOException, ServletException {
        String jsonInput = "{bad json}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getReader()).thenReturn(reader);
        when(response.getWriter()).thenReturn(writer);
        when(session.getAttribute(USER_KEY)).thenReturn(null);

        signInServlet.doPost(request, response);

        writer.flush();
        assertTrue(stringWriter.toString().contains("\"status\":\"error\""));
    }

    @Test
    public void testDoPost_UserAlreadyLoggedIn() throws IOException, ServletException {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(session.getAttribute(USER_KEY)).thenReturn(mockUser);
        when(request.getRequestDispatcher("/static/profile/profile_page.html")).thenReturn(requestDispatcher);

        signInServlet.doPost(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request, never()).getReader();
    }

}
