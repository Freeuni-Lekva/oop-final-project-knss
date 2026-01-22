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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.List;

import static com.example.constants.AttributeConstants.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AllSubmissionsServletTest {

    private AllSubmissionsServlet servlet;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private ServletContext context;
    @Mock private ServletConfig config;

    @Mock private UserDAO userDAO;
    @Mock private ProblemDAO problemDAO;
    @Mock private VerdictDAO verdictDAO;
    @Mock private CodeLanguageDAO codeLanguageDAO;
    @Mock private SubmissionDAO submissionDAO;

    private Gson gson;

    @Before
    public void setup() throws Exception {
        gson = new Gson();

        servlet = new AllSubmissionsServlet();
        when(config.getServletContext()).thenReturn(context);
        servlet.init(config);

        when(context.getAttribute(GSON_KEY)).thenReturn(gson);
        when(context.getAttribute(USER_DAO_KEY)).thenReturn(userDAO);
        when(context.getAttribute(PROBLEM_DAO_KEY)).thenReturn(problemDAO);
        when(context.getAttribute(VERDICT_DAO_KEY)).thenReturn(verdictDAO);
        when(context.getAttribute(CODE_LANGUAGE_DAO_KEY)).thenReturn(codeLanguageDAO);
        when(context.getAttribute(SUBMISSION_DAO_KEY)).thenReturn(submissionDAO);
    }

    @Test
    public void testDoGetReturnsSubmissionsJson() throws Exception {
        Submission submission = new Submission(
                1,                      // id
                1,                      // userId
                1,                      // problemId
                1,                      // verdictId
                "System.out.println(\"Hello\");", // solutionCode
                1,                      // codeLanguageId
                123L,                   // time
                456L,                   // memory
                new Timestamp(System.currentTimeMillis()), // submitDate
                "Passed"                // log
        );

        User user = new User();
        user.setUsername("testuser");

        when(submissionDAO.getAllSubmissionsByOrder()).thenReturn(List.of(submission));
        when(userDAO.getUser(1)).thenReturn(user);
        when(problemDAO.getProblemTitle(1)).thenReturn("Two Sum");
        when(codeLanguageDAO.getCodeLanguageById(1)).thenReturn(new CodeLanguage(1, "Java"));
        when(verdictDAO.getVerdictById(1)).thenReturn(new SubmissionVerdict(1, "Accepted"));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        writer.flush();
        String json = stringWriter.toString();

        assertTrue(json.contains("\"submissions\""));
        assertTrue(json.contains("testuser"));
        assertTrue(json.contains("Two Sum"));
        assertTrue(json.contains("Java"));
        assertTrue(json.contains("Accepted"));
        assertTrue(json.contains("System.out.println"));
    }
}
