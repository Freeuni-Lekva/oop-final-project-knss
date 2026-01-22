package com.example.problems.servlets;

import com.example.problems.DAO.DifficultyDAO;
import com.example.problems.DAO.ProblemDAO;
import com.example.problems.DAO.TopicDAO;
import com.example.problems.DTO.Difficulty;
import com.example.problems.DTO.Topic;
import com.example.problems.FrontResponse.ProblemListResponse;
import com.example.problems.utils.FilterCriteria;
import com.example.registration.DTO.User;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import static com.example.constants.AttributeConstants.*;
import static com.example.constants.SessionConstants.USER_KEY;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProblemsListServletTest {

    private ProblemsListServlet servlet;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private ServletContext servletContext;
    @Mock private ProblemDAO problemDAO;
    @Mock private DifficultyDAO difficultyDAO;
    @Mock private TopicDAO topicDAO;
    @Mock private HttpSession session;

    private User user;
    private Gson gson;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @Before
    public void setUp() throws Exception {
        servlet = new ProblemsListServlet() {
            @Override public ServletContext getServletContext() { return servletContext; }
        };

        gson = new Gson();
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);

        user = new User();
        user.setId(1);
        user.setUsername("testuser");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(USER_KEY)).thenReturn(user);

        when(servletContext.getAttribute(GSON_KEY)).thenReturn(gson);
        when(servletContext.getAttribute(PROBLEM_DAO_KEY)).thenReturn(problemDAO);
        when(servletContext.getAttribute(DIFFICULTY_DAO_KEY)).thenReturn(difficultyDAO);
        when(servletContext.getAttribute(TOPIC_DAO_KEY)).thenReturn(topicDAO);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    public void testDoPost_WithTitleFilterOnly() throws Exception {
        FilterCriteria criteria = new FilterCriteria("Ants", "", null, Collections.emptyList());

        ProblemListResponse problem = new ProblemListResponse();
        problem.setTitle("Ants");

        when(problemDAO.getProblemResponsesByFilterLoggedIn(any())).thenReturn(List.of(problem));

        mockJsonRequest(criteria);
        servlet.doPost(request, response);

        ProblemListResponse[] result = gson.fromJson(responseWriter.toString(), ProblemListResponse[].class);
        assertEquals(1, result.length);
        assertEquals("Ants", result[0].getTitle());

        verify(difficultyDAO, never()).getDifficultyByName(any());
        verify(topicDAO, never()).getTopicByName(any());
    }

    @Test
    public void testDoPost_WithDifficultyFilterOnly() throws Exception {
        FilterCriteria criteria = new FilterCriteria("", "", "Medium", Collections.emptyList());

        Difficulty difficulty = new Difficulty();
        difficulty.setId(2);
        difficulty.setDifficulty("Medium");

        when(difficultyDAO.getDifficultyByName("Medium")).thenReturn(difficulty);

        ProblemListResponse p1 = new ProblemListResponse();
        ProblemListResponse p2 = new ProblemListResponse();
        when(problemDAO.getProblemResponsesByFilterLoggedIn(any())).thenReturn(List.of(p1, p2));

        mockJsonRequest(criteria);
        servlet.doPost(request, response);

        ProblemListResponse[] result = gson.fromJson(responseWriter.toString(), ProblemListResponse[].class);
        assertEquals(2, result.length);
    }

    @Test
    public void testDoPost_WithMultipleTopics() throws Exception {
        FilterCriteria criteria = new FilterCriteria("", "", "", List.of("DP", "Graph"));

        when(topicDAO.getTopicByName("DP")).thenReturn(new Topic(1, "DP"));
        when(topicDAO.getTopicByName("Graph")).thenReturn(new Topic(2, "Graph"));

        ProblemListResponse p1 = new ProblemListResponse();
        ProblemListResponse p2 = new ProblemListResponse();
        ProblemListResponse p3 = new ProblemListResponse();
        when(problemDAO.getProblemResponsesByFilterLoggedIn(any())).thenReturn(List.of(p1, p2, p3));

        mockJsonRequest(criteria);
        servlet.doPost(request, response);

        ProblemListResponse[] result = gson.fromJson(responseWriter.toString(), ProblemListResponse[].class);
        assertEquals(3, result.length);
    }

    @Test
    public void testDoPost_WithNoFilters() throws Exception {
        FilterCriteria criteria = new FilterCriteria("", "", "", Collections.emptyList());
        when(problemDAO.getProblemResponsesByFilterLoggedIn(any())).thenReturn(Collections.emptyList());

        mockJsonRequest(criteria);
        servlet.doPost(request, response);

        assertEquals("[]", responseWriter.toString());
    }

    @Test
    public void testDoPost_WithIOException() throws Exception {
        when(request.getReader()).thenThrow(new IOException("Read error"));
        assertThrows(IOException.class, () -> servlet.doPost(request, response));
    }

    @Test
    public void testDoPost_WithInvalidJson() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader("invalid json"));
        when(request.getReader()).thenReturn(reader);
        assertThrows(Exception.class, () -> servlet.doPost(request, response));
    }

    private void mockJsonRequest(FilterCriteria criteria) throws IOException {
        String json = gson.toJson(criteria);
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);
    }
}
