package com.example.problems.servlets;

import com.example.problems.DAO.DifficultyDAO;
import com.example.problems.DTO.Difficulty;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.example.constants.AttributeConstants.*;

public class ProblemDifficultiesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DifficultyDAO difficultyDAO = (DifficultyDAO) getServletContext().getAttribute(DIFFICULTY_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);

        List<Difficulty> difficulties = difficultyDAO.getDifficulties();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(difficulties));
    }

}
