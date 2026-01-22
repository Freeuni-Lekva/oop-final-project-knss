package com.example.registration.servlets;

import com.example.registration.DTO.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;

import static com.example.constants.SessionConstants.USER_KEY;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Before
    public void setUp() {
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testRedirectProfileIfSignedIn_UserExists() throws IOException, ServletException {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        // Important: path matches exactly the one used in Authentication class
        when(session.getAttribute(USER_KEY)).thenReturn(mockUser);
        when(request.getRequestDispatcher("/static/profile/profile_page.html")).thenReturn(requestDispatcher);
        doNothing().when(requestDispatcher).forward(request, response);

        boolean result = Authentication.redirectProfileIfSignedIn(request, response);

        assertTrue(result);
        verify(request).getRequestDispatcher("/static/profile/profile_page.html");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testRedirectProfileIfSignedIn_UserNull() throws IOException, ServletException {
        when(session.getAttribute(USER_KEY)).thenReturn(null);

        boolean result = Authentication.redirectProfileIfSignedIn(request, response);

        assertFalse(result);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    public void testParseJsonBody_ValidJson() throws Exception {
        String jsonInput = "{\"username\":\"testuser\",\"password\":\"testpass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
        when(request.getReader()).thenReturn(reader);

        Method parseJsonBodyMethod = Authentication.class.getDeclaredMethod("parseJsonBody",
                HttpServletRequest.class, Class.class);
        parseJsonBodyMethod.setAccessible(true);

        TestUserSignInInput result = (TestUserSignInInput) parseJsonBodyMethod.invoke(null, request, TestUserSignInInput.class);

        assertNotNull(result);
        assertEquals("testuser", result.username);
        assertEquals("testpass", result.password);
    }

    @Test
    public void testParseJsonBody_EmptyJson() throws Exception {
        String jsonInput = "{}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
        when(request.getReader()).thenReturn(reader);

        Method parseJsonBodyMethod = Authentication.class.getDeclaredMethod("parseJsonBody",
                HttpServletRequest.class, Class.class);
        parseJsonBodyMethod.setAccessible(true);

        TestUserSignInInput result = (TestUserSignInInput) parseJsonBodyMethod.invoke(null, request, TestUserSignInInput.class);

        assertNotNull(result);
        assertNull(result.username);
        assertNull(result.password);
    }

    @Test
    public void testParseJsonBody_MultilineJson() throws Exception {
        String jsonInput = "{\n\"username\":\"testuser\",\n\"password\":\"testpass\"\n}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
        when(request.getReader()).thenReturn(reader);

        Method parseJsonBodyMethod = Authentication.class.getDeclaredMethod("parseJsonBody",
                HttpServletRequest.class, Class.class);
        parseJsonBodyMethod.setAccessible(true);

        TestUserSignInInput result = (TestUserSignInInput) parseJsonBodyMethod.invoke(null, request, TestUserSignInInput.class);

        assertNotNull(result);
        assertEquals("testuser", result.username);
        assertEquals("testpass", result.password);
    }

    @Test
    public void testSendJsonResponse_ValidObject() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        TestResponse testResponse = new TestResponse();
        testResponse.status = "ok";
        testResponse.message = "Success";

        Method sendJsonResponseMethod = Authentication.class.getDeclaredMethod("sendJsonResponse",
                HttpServletResponse.class, Object.class);
        sendJsonResponseMethod.setAccessible(true);

        sendJsonResponseMethod.invoke(null, response, testResponse);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        String jsonOutput = stringWriter.toString();
        assertTrue(jsonOutput.contains("\"status\":\"ok\""));
        assertTrue(jsonOutput.contains("\"message\":\"Success\""));
    }

    @Test
    public void testSendJsonResponse_NullObject() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        Method sendJsonResponseMethod = Authentication.class.getDeclaredMethod("sendJsonResponse",
                HttpServletResponse.class, Object.class);
        sendJsonResponseMethod.setAccessible(true);

        sendJsonResponseMethod.invoke(null, response, null);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        String jsonOutput = stringWriter.toString();
        assertEquals("null", jsonOutput);
    }

    @Test
    public void testSendJsonResponse_Map() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        Method sendJsonResponseMethod = Authentication.class.getDeclaredMethod("sendJsonResponse",
                HttpServletResponse.class, Object.class);
        sendJsonResponseMethod.setAccessible(true);

        sendJsonResponseMethod.invoke(null, response, map);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        String jsonOutput = stringWriter.toString();
        assertTrue(jsonOutput.contains("\"key1\":\"value1\""));
        assertTrue(jsonOutput.contains("\"key2\":\"value2\""));
    }

    // Authentication classes for testing JSON parsing
    static class TestUserSignInInput {
        String username;
        String password;
    }

    // Authentication class for JSON response testing
    static class TestResponse {
        String status;
        String message;
    }
}
