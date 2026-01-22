package com.example.submissions.servlets;

import com.example.problems.DAO.ProblemDAO;
import com.example.submissions.DAO.CodeLanguageDAO;
import com.example.submissions.DAO.SubmissionDAO;
import com.example.submissions.DAO.VerdictDAO;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.example.constants.AttributeConstants.*;
import static org.mockito.Mockito.*;

public class SubmissionsListServletTest {

    private SubmissionsListServlet servlet;

    @Mock
    private ServletContext servletContext;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ProblemDAO problemDAO;

    @Mock
    private SubmissionDAO submissionDAO;

    @Mock
    private VerdictDAO verdictDAO;

    @Mock
    private CodeLanguageDAO codeLanguageDAO;

    private Gson gson;

    private StringWriter responseWriter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        servlet = new SubmissionsListServlet();

        // Setup servletConfig to return our mocked servletContext
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        // Initialize servlet with mocked config
        servlet.init(servletConfig);

        // Setup servletContext attributes to return mocked DAOs
        when(servletContext.getAttribute(PROBLEM_DAO_KEY)).thenReturn(problemDAO);
        when(servletContext.getAttribute(SUBMISSION_DAO_KEY)).thenReturn(submissionDAO);
        when(servletContext.getAttribute(VERDICT_DAO_KEY)).thenReturn(verdictDAO);
        when(servletContext.getAttribute(CODE_LANGUAGE_DAO_KEY)).thenReturn(codeLanguageDAO);

        // Use real Gson instance, do NOT mock Gson (it's final)
        gson = new Gson();
        when(servletContext.getAttribute(GSON_KEY)).thenReturn(gson);

        // Setup request to return mocked session and servletContext
        when(request.getSession()).thenReturn(session);
        when(request.getServletContext()).thenReturn(servletContext);

        // Setup PrintWriter to capture servlet response output
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }


    @Test
    public void testDoGet_InvalidPath_ReturnsBadRequest() throws Exception {
        when(request.getPathInfo()).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
    }
}
