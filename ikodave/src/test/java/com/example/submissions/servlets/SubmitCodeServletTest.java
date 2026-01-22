package com.example.submissions.servlets;

import com.example.problems.DAO.ProblemDAO;
import com.example.problems.DTO.Problem;
import com.example.registration.DTO.User;
import com.example.submissions.CodeRunner.DockerCodeRunner;
import com.example.submissions.DAO.*;
import com.example.submissions.DTO.*;
import com.example.submissions.Utils.Submit.UserSubmission;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.*;

import java.io.*;
import java.util.List;

import static com.example.constants.AttributeConstants.*;
import static com.example.constants.SessionConstants.USER_KEY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class SubmitCodeServletTest {

    private SubmitCodeServlet servlet;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private ServletContext servletContext;
    @Mock private ServletConfig servletConfig;

    @Mock private ProblemDAO problemDAO;
    @Mock private TestDAO testDAO;
    @Mock private SubmissionDAO submissionDAO;
    @Mock private CodeLanguageDAO codeLanguageDAO;
    @Mock private VerdictDAO verdictDAO;
    @Mock private DockerCodeRunner dockerCodeRunner;

    private Gson gson;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @Before
    public void setUp() throws Exception {
        servlet = new SubmitCodeServlet();

        gson = new Gson();
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(printWriter);

        // Setup ServletContext via ServletConfig
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        servlet.init(servletConfig);

        // Inject mock attributes
        when(servletContext.getAttribute(PROBLEM_DAO_KEY)).thenReturn(problemDAO);
        when(servletContext.getAttribute(TEST_DAO_KEY)).thenReturn(testDAO);
        when(servletContext.getAttribute(SUBMISSION_DAO_KEY)).thenReturn(submissionDAO);
        when(servletContext.getAttribute(CODE_LANGUAGE_DAO_KEY)).thenReturn(codeLanguageDAO);
        when(servletContext.getAttribute(VERDICT_DAO_KEY)).thenReturn(verdictDAO);
        when(servletContext.getAttribute(DOCKER_CODE_RUNNER_KEY)).thenReturn(dockerCodeRunner);
        when(servletContext.getAttribute(GSON_KEY)).thenReturn(gson);
    }

    @Test
    public void testDoPost_UserLoggedInAndValidSubmission_ReturnsRedirectJson() throws Exception {
        // Simulate logged-in user
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        when(session.getAttribute(USER_KEY)).thenReturn(user);

        // Create UserSubmission using constructor
        UserSubmission submission = new UserSubmission(
                "System.out.println(\"Hello\");",
                "Java",
                "SampleProblem"
        );

        // Simulate request JSON body
        String jsonInput = gson.toJson(submission);
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
        when(request.getReader()).thenReturn(reader);

        // DAO mock setups
        Problem problem = new Problem();
        problem.setId(101);
        problem.setTitle("SampleProblem");
        when(problemDAO.getProblemByTitle("SampleProblem")).thenReturn(problem);

        when(testDAO.getTestCasesByProblemId(101)).thenReturn(List.of(
                new TestCase(1, 101, 1, "output", "input")
        ));

        CodeLanguage codeLanguage = new CodeLanguage(5, "Java");
        when(codeLanguageDAO.getCodeLanguageByName("Java")).thenReturn(codeLanguage);

        SubmissionVerdict runningVerdict = new SubmissionVerdict(1, "Running");
        when(verdictDAO.getVerdictByName("Running")).thenReturn(runningVerdict);

        when(submissionDAO.insertSubmission(any(Submission.class))).thenReturn(123);

        // Execute POST
        servlet.doPost(request, response);

        // Assert redirect output
        printWriter.flush();
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("/problems/submissions/SampleProblem"));

        verify(response).setContentType("application/json");
        verify(submissionDAO).insertSubmission(any(Submission.class));
    }
}
