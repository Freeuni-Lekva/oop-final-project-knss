package com.example.problems.servlets;

import com.example.problems.DAO.ProblemDAO;
import com.example.problems.DTO.Difficulty;
import com.example.problems.DTO.Status;
import com.example.problems.DTO.Topic;
import com.example.problems.Filters.*;
import com.example.problems.utils.FilterCriteria;
import com.example.registration.dao.UserDAO;
import com.example.registration.model.User;
import com.google.gson.Gson;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.util.AttributeConstants.*;
import static com.example.util.SessionConstants.USER_ID_KEY;

public class ProblemsListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/problems/html/problems.html")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = (String) request.getSession().getAttribute(USER_ID_KEY);
        UserDAO userDAO = (UserDAO) request.getAttribute(USER_DAO_KEY);
        ProblemDAO problemDAO = (ProblemDAO) request.getAttribute(PROBLEM_DAO_KEY);
        BasicDataSource basicDataSource = (BasicDataSource) request.getAttribute(BASIC_DATASOURCE_KEY);

        Gson gson = new Gson();
        FilterCriteria filterCriteria = gson.fromJson(request.getReader(), FilterCriteria.class);
        FilterAnd filterAnd = new FilterAnd();

        String titleString = filterCriteria.getTitle();
        if (!titleString.isEmpty()) {
            FilterTitle filterTitle = new FilterTitle(titleString);
            filterAnd.addFilter(filterTitle);
        }

        String difficultyString = filterCriteria.getDifficulty();
        if (!difficultyString.isEmpty()) {
            Difficulty difficulty = new Difficulty(problemDAO.getDifficultyId(difficultyString), difficultyString);
            FilterDifficulty filterDifficulty = new FilterDifficulty(difficulty);
            filterAnd.addFilter(filterDifficulty);
        }

        String statusString = filterCriteria.getStatus();
        if (!statusString.isEmpty() && userId != null) {
            User user = userDAO.getUser(Integer.parseInt(userId));
            Status status = new Status(problemDAO.getStatusId(statusString), statusString);
            FilterStatus filterStatus = new FilterStatus(user, status);
            filterAnd.addFilter(filterStatus);
        }

        List<String> topicStrings = filterCriteria.getTopics();
        List<Topic> topics = new ArrayList<>();
        for (String topicString : topicStrings) {
            Topic topic = new Topic(problemDAO.getTopicId(topicString), topicString);
            topics.add(topic);
        }

        if (!topics.isEmpty()) {
            FilterTopic filterTopic = new FilterTopic(topics);
            filterAnd.addFilter(filterTopic);
        }

        System.out.println(filterAnd.toSQLStatement());

        //List<Problem> problems = problemDAO.getProblemsByFilter(filterAnd);

        //response.setContentType("application/json");
        //response.setCharacterEncoding("UTF-8");
        //response.getWriter().write(gson.toJson(problems));
    }

}
