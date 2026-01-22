package com.example.submissions.servlets;

import com.example.problems.DAO.ProblemDAO;
import com.example.registration.dao.UserDAO;
import com.example.registration.DTO.User;
import com.example.submissions.DAO.CodeLanguageDAO;
import com.example.submissions.DAO.SubmissionDAO;
import com.example.submissions.DAO.VerdictDAO;
import com.example.submissions.DTO.CodeLanguage;
import com.example.submissions.DTO.Submission;
import com.example.submissions.DTO.SubmissionVerdict;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.List;

import static com.example.constants.AttributeConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubmissionsListDateServletTest {

    private SubmissionsListDateServlet servlet;

    @Mock private ServletConfig servletConfig;
    @Mock private ServletContext servletContext;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    @Mock private UserDAO userDAO;
    @Mock private ProblemDAO problemDAO;
    @Mock private SubmissionDAO submissionDAO;
    @Mock private VerdictDAO verdictDAO;
    @Mock private CodeLanguageDAO codeLanguageDAO;

    private Gson gson;
    private StringWriter responseWriter;

    @Before
    public void setUp() throws Exception {
        servlet = new SubmissionsListDateServlet();
        gson = new Gson();
        responseWriter = new StringWriter();

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(USER_DAO_KEY)).thenReturn(userDAO);
        when(servletContext.getAttribute(PROBLEM_DAO_KEY)).thenReturn(problemDAO);
        when(servletContext.getAttribute(SUBMISSION_DAO_KEY)).thenReturn(submissionDAO);
        when(servletContext.getAttribute(VERDICT_DAO_KEY)).thenReturn(verdictDAO);
        when(servletContext.getAttribute(CODE_LANGUAGE_DAO_KEY)).thenReturn(codeLanguageDAO);
        when(servletContext.getAttribute(GSON_KEY)).thenReturn(gson);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        servlet.init(servletConfig);
    }
    @Test
    public void testDoPost_ReturnsFilteredSubmissionsJson() throws Exception {
        // Prepare input JSON for filter body
        String inputJson = "{\"username\":\"testuser\",\"day\":1,\"month\":7,\"year\":2025}";
        BufferedReader reader = new BufferedReader(new StringReader(inputJson));
        when(request.getReader()).thenReturn(reader);

        // Mock user returned by userDAO
        User user = new User();
        user.setId(10);
        user.setUsername("testuser");
        when(userDAO.getUserByUsername("testuser")).thenReturn(user);

        // Mock submission returned by submissionDAO
        Submission submission = new Submission(
                1, 10, 20, 2, "System.out.println(\"Hello\");", 3,
                150L, 512L,
                new Timestamp(System.currentTimeMillis()),
                "Accepted"
        );
        when(submissionDAO.getSubmissionsByDateByOrder(10, 1, 7, 2025)).thenReturn(List.of(submission));

        // Mock related DAOs return values
        when(problemDAO.getProblemTitle(20)).thenReturn("Sample Problem");
        when(codeLanguageDAO.getCodeLanguageById(3)).thenReturn(new CodeLanguage(3, "Java"));
        when(verdictDAO.getVerdictById(2)).thenReturn(new SubmissionVerdict(2, "Accepted"));

        // Execute servlet method
        servlet.doPost(request, response);
        responseWriter.flush();

        String jsonResponse = responseWriter.toString();
        System.out.println("JSON Response:\n" + jsonResponse);  // For debugging

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        // Relaxed assertions to avoid escaping issues
        assertTrue(jsonResponse.contains("\"testuser\""));
        assertTrue(jsonResponse.contains("\"Sample Problem\""));
        assertTrue(jsonResponse.contains("\"Java\""));
        assertTrue(jsonResponse.contains("\"Accepted\""));
        assertTrue(jsonResponse.contains("System.out.println"));
    }

    @Test
    public void testDoPost_EmptySubmissionList() throws Exception {
        String inputJson = "{\"username\":\"testuser\",\"day\":1,\"month\":7,\"year\":2025}";
        BufferedReader reader = new BufferedReader(new StringReader(inputJson));
        when(request.getReader()).thenReturn(reader);

        User user = new User();
        user.setId(10);
        user.setUsername("testuser");
        when(userDAO.getUserByUsername("testuser")).thenReturn(user);

        when(submissionDAO.getSubmissionsByDateByOrder(10, 1, 7, 2025)).thenReturn(List.of());

        servlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("\"submissions\":[]"));
    }

    @Test(expected = com.google.gson.JsonSyntaxException.class)
    public void testDoPost_MalformedJson() throws Exception {
        String inputJson = "{ malformed json ";
        BufferedReader reader = new BufferedReader(new StringReader(inputJson));
        when(request.getReader()).thenReturn(reader);

        servlet.doPost(request, response);
    }
}
