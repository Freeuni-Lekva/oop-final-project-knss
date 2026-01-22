package com.example.submissions.servlets;

import com.example.submissions.DAO.CodeLanguageDAO;
import com.example.submissions.DTO.CodeLanguage;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.example.constants.AttributeConstants.CODE_LANGUAGE_DAO_KEY;
import static com.example.constants.AttributeConstants.GSON_KEY;

public class CodeLanguagesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CodeLanguageDAO codeLanguageDAO = (CodeLanguageDAO) getServletContext().getAttribute(CODE_LANGUAGE_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);

        List<CodeLanguage> codeLanguages = codeLanguageDAO.getCodeLanguages();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(codeLanguages));
    }

}
