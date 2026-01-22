package com.example.admin.servlets;

import com.example.admin.dao.ProblemTopicRelationDAO;
import com.example.admin.dto.ProblemWithTests;
import com.example.problems.DAO.DifficultyDAO;
import com.example.problems.DAO.ProblemDAO;
import com.example.problems.DAO.TopicDAO;
import com.example.problems.DTO.Difficulty;
import com.example.problems.DTO.Problem;
import com.example.problems.DTO.Topic;
import com.example.submissions.DAO.TestDAO;
import com.example.submissions.DTO.TestCase;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.example.constants.AttributeConstants.*;

@WebServlet(name = "AddProblemServlet", value = "/api/AddProblemServlet")
public class AddProblemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AdminAuthHelper.isAdmin(request, getServletContext())) {
            response.sendRedirect("/home");
            return;
        }

        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);
        ProblemDAO problemDAO = (ProblemDAO) getServletContext().getAttribute(PROBLEM_DAO_KEY);
        TestDAO testDAO = (TestDAO) getServletContext().getAttribute(TEST_DAO_KEY);
        DifficultyDAO difficultyDAO = (DifficultyDAO) getServletContext().getAttribute(DIFFICULTY_DAO_KEY);
        ProblemTopicRelationDAO problemTopicRelationDAO = (ProblemTopicRelationDAO) getServletContext().getAttribute(PROBLEM_TOPIC_RELATION_DAO_KEY);
        TopicDAO topicDAO = (TopicDAO) getServletContext().getAttribute(TOPIC_DAO_KEY);

        ProblemWithTests problemWithTests = gson.fromJson(request.getReader(), ProblemWithTests.class);

        // Validate difficulty
        Difficulty difficulty = difficultyDAO.getDifficultyByName(problemWithTests.getDifficulty());
        if (difficulty == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid difficulty: " + problemWithTests.getDifficulty());
            return;
        }


        // Build and insert problem
        Problem problem = new Problem();
        problem.setTitle(problemWithTests.getTitle());
        problem.setDescription(problemWithTests.getDescription());
        problem.setInputSpec(problemWithTests.getInputSpec());
        problem.setOutputSpec(problemWithTests.getOutputSpec());
        problem.setDifficultyId(difficulty.getId());
        problem.setTimeLimit(problemWithTests.getTimeLimit());
        problem.setMemoryLimit(128 * 1024 * 1024); // 128 MB default
        problem.setCreateDate(new Timestamp(System.currentTimeMillis()));

        problemDAO.insertProblem(problem);
        int problemId = problemDAO.getProblemId(problem.getTitle());

        // insert topic relations
        for (String topicName : problemWithTests.getTopics()) {
            Topic topic = topicDAO.getTopicByName(topicName);
            if (topic == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid topic: " + topicName);
                return;
            }
            problemTopicRelationDAO.insertProblemTopicRelation(problemId, topic.getId());
        } // TODO be carefull with this code



        // Build and insert test cases
        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < problemWithTests.getTestCases().size(); i++) {
            TestCase testCase = new TestCase();
            testCase.setProblemId(problemId);
            testCase.setProblemInput(problemWithTests.getTestCases().get(i).getInput());
            testCase.setProblemOutput(problemWithTests.getTestCases().get(i).getOutput());
            testCase.setTestNumber(i + 1);
            testCases.add(testCase);
        }

        testCases.forEach(testDAO::insertTestCase);

        // Send success
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
