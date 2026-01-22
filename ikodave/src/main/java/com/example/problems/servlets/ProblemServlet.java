package com.example.problems.servlets;

import com.example.problems.DAO.ProblemDAO;
import com.example.problems.DTO.Problem;
import com.example.problems.DTO.Topic;
import com.example.problems.FrontResponse.ProblemSpecificResponse;
import com.example.problems.utils.ProblemTitle;
import com.example.registration.DTO.User;
import com.example.submissions.DAO.TestDAO;
import com.example.submissions.DTO.TestCase;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.example.constants.AttributeConstants.*;
import static com.example.constants.SessionConstants.USER_KEY;

public class ProblemServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute(USER_KEY);
        ProblemDAO problemDAO = (ProblemDAO) getServletContext().getAttribute(PROBLEM_DAO_KEY);
        TestDAO testDAO = (TestDAO) getServletContext().getAttribute(TEST_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);

        ProblemTitle problemTitle = gson.fromJson(request.getReader(), ProblemTitle.class);

        String title = problemTitle.getProblemTitle();


        if (title == null || title.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty 'title' field.");
            return;
        }

        Problem problem = problemDAO.getProblemByTitle(title);

        List<TestCase> testCases = testDAO.getTestCasesByProblemId(problem.getId());

        ProblemSpecificResponse problemSpecificResponse = new ProblemSpecificResponse(
                problem.getTitle(),
                problem.getDescription(),
                user != null? problemDAO.getProblemStatus(problem.getId(), user.getId()) : "No Status",
                problemDAO.getProblemTopics(problem.getId()).stream().map(Topic::getTopic).toList(),
                problemDAO.getProblemDifficulty(problem.getId()).getDifficulty(),
                testCases.subList(0, Math.min(2, testCases.size())),
                problem.getInputSpec(),
                problem.getOutputSpec(),
                problem.getTimeLimit(),
                problem.getMemoryLimit()
        );

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(problemSpecificResponse));
    }

}
