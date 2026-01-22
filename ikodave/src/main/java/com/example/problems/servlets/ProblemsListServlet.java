package com.example.problems.servlets;

import com.example.problems.DAO.DifficultyDAO;
import com.example.problems.DAO.ProblemDAO;
import com.example.problems.DAO.TopicDAO;
import com.example.problems.DTO.Difficulty;
import com.example.problems.DTO.Topic;
import com.example.problems.Filters.*;
import com.example.problems.FrontResponse.ProblemListResponse;
import com.example.problems.utils.FilterCriteria;
import com.example.registration.DTO.User;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.constants.AttributeConstants.*;
import static com.example.constants.SessionConstants.USER_KEY;

public class ProblemsListServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute(USER_KEY);
//        User user = new User(1, "x", "y", 1, new Date());
        ProblemDAO problemDAO = (ProblemDAO) getServletContext().getAttribute(PROBLEM_DAO_KEY);
        DifficultyDAO difficultyDAO = (DifficultyDAO) getServletContext().getAttribute(DIFFICULTY_DAO_KEY);
        TopicDAO topicDAO = (TopicDAO) getServletContext().getAttribute(TOPIC_DAO_KEY);
        Gson gson = (Gson) getServletContext().getAttribute(GSON_KEY);

        FilterCriteria filterCriteria = gson.fromJson(request.getReader(), FilterCriteria.class);

        FilterAnd filterAnd;
        if (user != null) {
            filterAnd = new FilterAndLoggedIn();
        }
        else {
            filterAnd = new FilterAndLoggedOut();
        }


        String titleString = filterCriteria.getTitle();
        if (!titleString.isEmpty()) {
            filterAnd.addFilter(new FilterTitle(titleString));
        }

        String difficultyName = filterCriteria.getDifficulty();
        if (difficultyName != null) {
            Difficulty difficulty = difficultyDAO.getDifficultyByName(difficultyName);
            FilterDifficulty filterDifficulty = new FilterDifficulty(difficulty);
            filterAnd.addFilter(filterDifficulty);
        }

        String statusName = filterCriteria.getStatus();


        if (statusName == null && user != null) {
            FilterStatusNone filterStatusNone = new FilterStatusNone();
            filterStatusNone.addFilter(new FilterStatusSolved(user));
            filterStatusNone.addFilter(new FilterStatusAttempted(user));
            filterStatusNone.addFilter(new FilterStatusTodo(user));

            filterAnd.addFilter(filterStatusNone);
        }

        if (statusName != null && user != null) {
            if (statusName.equalsIgnoreCase("Solved")) {
                FilterStatusSolved filterStatusSolved = new FilterStatusSolved(user);
                filterAnd.addFilter(filterStatusSolved);
            }

            if (statusName.equalsIgnoreCase("Attempted")) {
               FilterStatusAttempted filterStatusAttempted = new FilterStatusAttempted(user);
               filterAnd.addFilter(filterStatusAttempted);
            }

            if (statusName.equalsIgnoreCase("Todo")) {
                FilterStatusTodo filterStatusTodo = new FilterStatusTodo(user);
                filterAnd.addFilter(filterStatusTodo);
            }
        }


        List<String> topicNames = filterCriteria.getTopics();
        List<Topic> topics = new ArrayList<>();
        for (String topicName : topicNames) {
            Topic topic = topicDAO.getTopicByName(topicName);
            topics.add(topic);
        }

        if (!topics.isEmpty()) {
            FilterTopic filterTopic = new FilterTopic(topics);
            filterAnd.addFilter(filterTopic);
        }


        List<ProblemListResponse> problems;

        if (user != null) {
            problems = problemDAO.getProblemResponsesByFilterLoggedIn(filterAnd);
        }
        else {
            problems = problemDAO.getProblemResponsesByFilterLoggedOut(filterAnd);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(problems));
    }

}
