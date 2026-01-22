package com.example.submissions.servlets;

import com.example.submissions.DAO.CodeLanguageDAO;
import com.example.submissions.DTO.CodeLanguage;
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
import java.util.Arrays;
import java.util.List;

import static com.example.constants.AttributeConstants.CODE_LANGUAGE_DAO_KEY;
import static com.example.constants.AttributeConstants.GSON_KEY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CodeLanguagesServletTest {

    private CodeLanguagesServlet servlet;

    @Mock private ServletConfig servletConfig;
    @Mock private ServletContext servletContext;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private CodeLanguageDAO codeLanguageDAO;

    private Gson gson;
    private StringWriter responseWriter;

    @Before
    public void setUp() throws Exception {
        servlet = new CodeLanguagesServlet();
        gson = new Gson();
        responseWriter = new StringWriter();

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(CODE_LANGUAGE_DAO_KEY)).thenReturn(codeLanguageDAO);
        when(servletContext.getAttribute(GSON_KEY)).thenReturn(gson);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        servlet.init(servletConfig);
    }

    @Test
    public void testDoGet_ReturnsCodeLanguagesJson() throws Exception {
        // Arrange
        List<CodeLanguage> mockLanguages = Arrays.asList(
                new CodeLanguage(1, "Java"),
                new CodeLanguage(2, "Python")
        );
        when(codeLanguageDAO.getCodeLanguages()).thenReturn(mockLanguages);


        servlet.doGet(request, response);


        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        String output = responseWriter.toString();
        assertTrue(output.contains("\"id\":1"));
        assertTrue(output.contains("\"language\":\"Java\""));
        assertTrue(output.contains("\"language\":\"Python\""));
    }

    @Test
    public void testDoGet_EmptyList() throws Exception {
        when(codeLanguageDAO.getCodeLanguages()).thenReturn(List.of());

        servlet.doGet(request, response);

        String output = responseWriter.toString();
        assertTrue(output.equals("[]"));
    }

    @Test(expected = NullPointerException.class)
    public void testDoGet_NullDAO() throws Exception {
        when(servletContext.getAttribute(CODE_LANGUAGE_DAO_KEY)).thenReturn(null);

        servlet.doGet(request, response);
    }
}
