package com.example.problems.servlets;

import com.example.problems.DAO.ProblemDAO;
import com.example.problems.DTO.*;
import com.example.problems.FrontResponse.ProblemSpecificResponse;
import com.example.problems.utils.ProblemTitle;
import com.example.registration.DTO.User;
import com.example.submissions.DAO.TestDAO;
import com.example.submissions.DTO.TestCase;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static com.example.constants.AttributeConstants.*;
import static com.example.constants.SessionConstants.USER_KEY;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProblemServletTest {

    private ProblemServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServletContext servletContext;

    @Mock
    private ProblemDAO problemDAO;

    @Mock
    private TestDAO testDAO;

    private Gson gson;
    private StringWriter responseWriter;
    private PrintWriter printWriter;
    private User testUser;

    @Before
    public void setUp() throws Exception {
        servlet = new ProblemServlet() {
            @Override
            public ServletContext getServletContext() {
                return servletContext;
            }
        };

        gson = new Gson();
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);

        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");

        when(request.getSession()).thenReturn(session);
        when(servletContext.getAttribute(GSON_KEY)).thenReturn(gson);
        when(servletContext.getAttribute(PROBLEM_DAO_KEY)).thenReturn(problemDAO);
        when(servletContext.getAttribute(TEST_DAO_KEY)).thenReturn(testDAO);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    public void testDoPost_WithValidTitleAndAuthenticatedUser() throws Exception {
        when(session.getAttribute(USER_KEY)).thenReturn(testUser);

        ProblemTitle problemTitle = new ProblemTitle("Two Sum");
        String jsonRequest = gson.toJson(problemTitle);
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest));
        when(request.getReader()).thenReturn(reader);

        Problem problem = new Problem();
        problem.setId(1);
        problem.setTitle("Two Sum");
        problem.setDescription("Given an array of integers...");
        problem.setInputSpec("Array of integers");
        problem.setOutputSpec("Indices of two numbers");
        problem.setTimeLimit(1000);
        problem.setMemoryLimit(256);

        when(problemDAO.getProblemByTitle("Two Sum")).thenReturn(problem);

        Status status = new Status(1, "Solved");
        when(problemDAO.getProblemStatus(1, 1)).thenReturn(String.valueOf(status));

        List<Topic> topics = Arrays.asList(
                new Topic(1, "Array"),
                new Topic(2, "Hash Table")
        );
        when(problemDAO.getProblemTopics(1)).thenReturn(topics);

        Difficulty difficulty = new Difficulty(1, "Easy");
        when(problemDAO.getProblemDifficulty(1)).thenReturn(difficulty);

        List<TestCase> testCases = Arrays.asList(
                new TestCase(1, 1, 1, "[2,7,11,15]", "[0,1]"),
                new TestCase(2, 1, 2, "[3,2,4]", "[1,2]")
        );
        when(testDAO.getTestCasesByProblemId(1)).thenReturn(testCases);

        servlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        printWriter.flush();

        String responseJson = responseWriter.toString();
        ProblemSpecificResponse actualResponse = gson.fromJson(responseJson, ProblemSpecificResponse.class);

        assertEquals("Two Sum", actualResponse.getProblemTitle());
        assertEquals("Given an array of integers...", actualResponse.getProblemDescription());
        assertEquals(2, actualResponse.getProblemTopics().size());
        assertEquals("Easy", actualResponse.getProblemDifficulty());
        assertEquals(2, actualResponse.getProblemTestCases().size());
    }

    @Test
    public void testDoPost_WithEmptyTitle() throws Exception {
        when(session.getAttribute(USER_KEY)).thenReturn(testUser);

        ProblemTitle problemTitle = new ProblemTitle("");
        String jsonRequest = gson.toJson(problemTitle);
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest));
        when(request.getReader()).thenReturn(reader);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty 'title' field.");
        verify(problemDAO, never()).getProblemByTitle(anyString());
    }

    @Test
    public void testDoPost_WithValidTitleAndNoUser() throws Exception {
        when(session.getAttribute(USER_KEY)).thenReturn(null);

        ProblemTitle problemTitle = new ProblemTitle("Binary Search");
        String jsonRequest = gson.toJson(problemTitle);
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest));
        when(request.getReader()).thenReturn(reader);

        Problem problem = new Problem();
        problem.setId(2);
        problem.setTitle("Binary Search");
        problem.setDescription("Search in sorted array");
        problem.setInputSpec("Sorted array and target");
        problem.setOutputSpec("Index of target");
        problem.setTimeLimit(1000);
        problem.setMemoryLimit(256);

        when(problemDAO.getProblemByTitle("Binary Search")).thenReturn(problem);

        // Removed unnecessary stubbing:
        // when(problemDAO.getProblemStatus(2, 0)).thenReturn(null);

        List<Topic> topics = Arrays.asList(new Topic(1, "Binary Search"));
        when(problemDAO.getProblemTopics(2)).thenReturn(topics);

        Difficulty difficulty = new Difficulty(2, "Medium");
        when(problemDAO.getProblemDifficulty(2)).thenReturn(difficulty);

        List<TestCase> testCases = Arrays.asList(
                new TestCase(4, 2, 1, "[1,2,3,4,5]", "2"),
                new TestCase(5, 2, 2, "[1,3,5,7,9]", "-1")
        );
        when(testDAO.getTestCasesByProblemId(2)).thenReturn(testCases);

        servlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        printWriter.flush();

        ProblemSpecificResponse actualResponse = gson.fromJson(responseWriter.toString(), ProblemSpecificResponse.class);

        assertEquals("Binary Search", actualResponse.getProblemTitle());
        assertEquals("Search in sorted array", actualResponse.getProblemDescription());
        assertEquals("No Status", actualResponse.getProblemStatus());
        assertEquals(1, actualResponse.getProblemTopics().size());
        assertEquals("Medium", actualResponse.getProblemDifficulty());
        assertEquals(2, actualResponse.getProblemTestCases().size());
    }
}
