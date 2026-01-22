package com.example.admin.servlets;

import com.example.problems.DAO.DifficultyDAO;
import com.example.problems.DAO.ProblemDAO;
import com.example.problems.DTO.Difficulty;
import com.example.problems.DTO.Problem;
import com.example.submissions.DAO.TestDAO;
import com.example.submissions.DTO.TestCase;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.StringReader;

import static com.example.constants.AttributeConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AddProblemServletTest {

    private AddProblemServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;

    private Gson gson;
    private ProblemDAO problemDAO;
    private TestDAO testDAO;
    private DifficultyDAO difficultyDAO;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AddProblemServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        servletContext = mock(ServletContext.class);

        gson = new Gson();
        problemDAO = mock(ProblemDAO.class);
        testDAO = mock(TestDAO.class);
        difficultyDAO = mock(DifficultyDAO.class);

        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(GSON_KEY)).thenReturn(gson);
        when(servletContext.getAttribute(PROBLEM_DAO_KEY)).thenReturn(problemDAO);
        when(servletContext.getAttribute(TEST_DAO_KEY)).thenReturn(testDAO);
        when(servletContext.getAttribute(DIFFICULTY_DAO_KEY)).thenReturn(difficultyDAO);

        ServletConfig config = mock(ServletConfig.class);
        when(config.getServletContext()).thenReturn(servletContext);
        servlet.init(config);
    }


    @Test
    void testDoPost_Success() throws Exception {
        // Arrange input JSON
        String json = "{\n" +
                "  \"title\": \"Sum Problem\",\n" +
                "  \"description\": \"Calculate sum\",\n" +
                "  \"inputSpec\": \"Two integers\",\n" +
                "  \"outputSpec\": \"Sum\",\n" +
                "  \"difficulty\": \"Easy\",\n" +
                "  \"timeLimit\": 1000,\n" +
                "  \"testCases\": [\n" +
                "    { \"input\": \"1 2\", \"output\": \"3\" },\n" +
                "    { \"input\": \"5 7\", \"output\": \"12\" }\n" +
                "  ]\n" +
                "}";

        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        // Difficulty exists
        Difficulty difficulty = new Difficulty();
        difficulty.setId(1);
        difficulty.setDifficulty("Easy");
        when(difficultyDAO.getDifficultyByName("Easy")).thenReturn(difficulty);

        // Return problem id when inserted
        when(problemDAO.getProblemId("Sum Problem")).thenReturn(42);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(problemDAO).insertProblem(any(Problem.class));
        verify(problemDAO).getProblemId("Sum Problem");
        verify(testDAO, times(2)).insertTestCase(any(TestCase.class));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoPost_InvalidDifficulty_SendsBadRequest() throws Exception {
        String json = "{ \"difficulty\": \"Impossible\" }";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        when(difficultyDAO.getDifficultyByName("Impossible")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), contains("Invalid difficulty"));
        verifyNoInteractions(problemDAO);
        verifyNoInteractions(testDAO);
    }

}
